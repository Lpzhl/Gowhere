package com.atguigu.service.impl;

import com.atguigu.mapper.HotelImagesMapper;
import com.atguigu.mapper.RoomTypesMapper;
import com.atguigu.pojo.HotelImages;
import com.atguigu.pojo.RoomTypes;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.atguigu.vo.BookingResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Bookings;
import com.atguigu.service.BookingsService;
import com.atguigu.mapper.BookingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
* @author LoveF
* @description 针对表【bookings】的数据库操作Service实现
* @createDate 2023-12-03 14:35:31
*/
@Service
public class BookingsServiceImpl extends ServiceImpl<BookingsMapper, Bookings>
    implements BookingsService{

    @Autowired
    private RoomTypesMapper roomTypesMapper;

    @Autowired
    private BookingsMapper bookingsMapper;

    @Autowired
    private HotelImagesMapper hotelImagesMapper;  //酒店图片Mapper

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public List<Bookings> getBookingsByUserAndHotel(Long userId, Long hotelId) {
        QueryWrapper<Bookings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("hotel_id", hotelId);
        return list(queryWrapper);
    }

    @Override
    public List<Bookings> getAllBookings() {
        QueryWrapper<Bookings> bookingsQueryWrapper = new QueryWrapper<>();
        bookingsQueryWrapper.ne("booking_status", "已过期");

        return bookingsMapper.selectList(bookingsQueryWrapper);
    }


    /**
     * 更新订单信息
     * @param bookings
     * @return
     */
    @Override
    public boolean updateBooking(Bookings bookings) {
        if (Integer.parseInt(String.valueOf(bookings.getTotalPrice())) < 0) {
            // 如果总价小于0，更新订单并设置为待评价状态
            bookings.setBookingStatus("待评价");
        } else {
            // 如果总价大于等于0，更新订单并设置为待支付状态
            bookings.setBookingStatus("待支付");
            // 设置订单的过期时间（30分钟后）
            long expireAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
            stringRedisTemplate.opsForZSet().add("bookings", bookings.getBookingId().toString(), expireAt);

            long remainingTime = expireAt - System.currentTimeMillis();
        }

        return this.updateById(bookings);
    }


    /**
     * 获取指定用户的订单信息
     * @param
     * @return
     */
    @Override
    public List<Bookings> getUserOrders(int userId) {
        QueryWrapper<Bookings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    @Override
    public List<Bookings> getBookingsByHotelId(int hotelId) {

        QueryWrapper<Bookings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id", hotelId);
        return bookingsMapper.selectList(queryWrapper);
    }



    /**
     * 自动更新订单是否过期
     *
     */
    @Scheduled(fixedRate = 60000)  // 每分钟执行一次
    public void processExpiredBookings() {
        long currentTime = System.currentTimeMillis();
        Set<String> expiredBookings = stringRedisTemplate.opsForZSet().rangeByScore("bookings", 0, currentTime);
        System.out.println("========================");
        for (String bookingId : expiredBookings) {
            // 开始事务
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

            try {
                // 查询并更新订单状态为“已过期”

                Bookings bookings = bookingsMapper.selectById(bookingId);
                Integer roomTypeId = bookings.getRoomTypeId();
                bookings.setBookingStatus("已过期");
                RoomTypes roomTypes = roomTypesMapper.selectById(roomTypeId);

                //还原数量
                roomTypes.setAvailableRooms(Integer.valueOf(roomTypes.getAvailableRooms()+bookings.getRoomNumber()));

                UpdateWrapper<Bookings> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("booking_id", bookingId);
                update(bookings, updateWrapper);
                System.out.println("==========自动更新订单状态执行==============");

                // 从Redis中移除
                stringRedisTemplate.opsForZSet().remove("bookings", bookingId);

                // 提交事务
                transactionManager.commit(status);
            } catch (Exception e) {
                // 回滚事务
                transactionManager.rollback(status);
                // 记录错误或采取其他补救措施
            }
        }
    }


    /**
     * 预定酒店订单
     * @param booking
     * @return
     */
    @Override
    @Transactional   //事务回滚
    public Result createBooking(Bookings booking) {
        System.out.println("预定的booking = " + booking);
        RoomTypes roomType = roomTypesMapper.selectById(booking.getRoomTypeId());

        System.out.println("roomType：" + roomType.getAvailableRooms());

        if (roomType != null && (roomType.getAvailableRooms() - Integer.parseInt(booking.getRoomNumber())) >= 0) {
            // 使用乐观锁更新房间数量
            UpdateWrapper<RoomTypes> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("room_type_id", roomType.getRoomTypeId())
                    .set("available_rooms", roomType.getAvailableRooms() - Integer.parseInt(booking.getRoomNumber()))
                    .set("version", roomType.getVersion() + 1)
                    .eq("version", roomType.getVersion());

            boolean updated = roomTypesMapper.update(roomType, updateWrapper) > 0;
            if (updated) {
                booking.setBookingStatus("待支付");

                // 查询与此酒店相关联的封面图片
                QueryWrapper<HotelImages> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("hotel_id", booking.getHotelId())
                        .eq("image_type", "cover")
                        .orderByAsc(String.valueOf(booking.getHotelId()))  // 随机排序
                        .last("LIMIT 1");      // 只获取一张图片

                HotelImages hotelImage = hotelImagesMapper.selectOne(queryWrapper);
                if (hotelImage != null) {
                    // 设置图片 URL
                    booking.setImageUrl(hotelImage.getImageUrl());
                }


                bookingsMapper.insert(booking);


                long expireAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                stringRedisTemplate.opsForZSet().add("bookings", booking.getBookingId().toString(), expireAt);


                long remainingTime = expireAt - System.currentTimeMillis();

                // 创建包含订单信息和剩余时间的响应对象
                BookingResponse response = new BookingResponse();
                response.setBooking(booking);
                response.setRemainingTime(remainingTime);
                return Result.ok(response);
            } else {
                // 更新失败，可能是因为乐观锁
                return Result.build("房间已被预订，请重试");
            }
        } else {
            return Result.build(null, ResultCodeEnum.KUCHUNBUZHU);
        }
    }

    @Override
    public Result getOrderInfo(int bookingId) {
        // 从数据库获取订单信息
        Bookings booking = bookingsMapper.selectById(bookingId);

        // 将bookingId转换为字符串
        String bookingIdStr = String.valueOf(bookingId);

        // 从Redis获取过期时间
        Double expireAt = stringRedisTemplate.opsForZSet().score("bookings", bookingIdStr);
        long remainingTime = 0;
        if (expireAt != null) {
            remainingTime = Math.max(0, expireAt.longValue() - System.currentTimeMillis());
        }

        // 创建响应对象
        BookingResponse response = new BookingResponse();
        response.setBooking(booking);
        response.setRemainingTime(remainingTime);
        System.out.println("response = " + response);
        return Result.ok(response);
    }


    /**
     * 跟新订单状态
     * @param bookingId
     * @param status
     * @return
     */
    @Override
    public boolean updateBookingStatus(int bookingId, String status) {
        Bookings bookings = new Bookings();
        bookings.setBookingStatus(status);

        UpdateWrapper<Bookings> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("booking_id", bookingId);

        return update(bookings, updateWrapper);
    }


    /**
     * 过期订单
     * @param bookingId
     * @return
     */
    @Override
    public Result handleExpiredBooking(int bookingId) {
        // 1. 更新订单状态为"已过期"
        Bookings booking = bookingsMapper.selectById(bookingId);
        if (booking != null) {
            booking.setBookingStatus("已过期");
            bookingsMapper.updateById(booking);

            // 还原房间数量
            RoomTypes roomType = roomTypesMapper.selectById(booking.getRoomTypeId());
            if (roomType != null) {
                roomType.setAvailableRooms(roomType.getAvailableRooms() + Integer.parseInt(booking.getRoomNumber()));
                roomTypesMapper.updateById(roomType);
            }

            // 从Redis移除相关的过期时间数据
            stringRedisTemplate.opsForZSet().remove("bookings", String.valueOf(booking.getBookingId()));

            return Result.ok("订单已标记为过期");
        } else {
            return Result.build(null, ResultCodeEnum.UN_FIND_ORDER);
        }
    }

    /**
     * 取消订单
     * @param bookingId
     * @return
     */
    @Override
    public Result handleOffBooking(int bookingId) {
        // 1. 更新订单状态为"已取消"
        Bookings booking = bookingsMapper.selectById(bookingId);
        if (booking != null) {
            booking.setBookingStatus("已取消");
            bookingsMapper.updateById(booking);

            // 还原房间数量
            RoomTypes roomType = roomTypesMapper.selectById(booking.getRoomTypeId());
            if (roomType != null) {
                roomType.setAvailableRooms(roomType.getAvailableRooms() + Integer.parseInt(booking.getRoomNumber()));
                roomTypesMapper.updateById(roomType);
            }

            // 从Redis移除相关的过期时间数据
            stringRedisTemplate.opsForZSet().remove("bookings", String.valueOf(booking.getBookingId()));

            //返回取消订单的数据
            return Result.ok(booking);
        } else {
            return Result.build(null, ResultCodeEnum.UN_FIND_ORDER);
        }
    }


}




