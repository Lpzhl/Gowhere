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
                Bookings bookings = new Bookings();
                bookings.setBookingStatus("已过期");

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

                /**
                 *  在这里补充 设置一个image_url
                 *  根据booking.getHotelId() 去hotel_images表中随机获取第一张且image_type为cover的图片
                 *  并且把获取到的url插入到 booking.setImageUrl();
                 *
                 */

                bookingsMapper.insert(booking);

                // 设置订单的过期时间（30分钟后）
                long expireAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                stringRedisTemplate.opsForZSet().add("bookings", booking.getBookingId().toString(), expireAt);

                // 计算剩余时间（毫秒）
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




