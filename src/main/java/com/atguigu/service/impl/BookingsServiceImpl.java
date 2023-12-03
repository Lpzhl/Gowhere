package com.atguigu.service.impl;

import com.atguigu.mapper.RoomTypesMapper;
import com.atguigu.pojo.RoomTypes;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Bookings;
import com.atguigu.service.BookingsService;
import com.atguigu.mapper.BookingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional  // 事务回滚  这个注解会确保方法中的所有数据库操作要么全部成功，要么在遇到异常时全部回滚
    public Result createBooking(Bookings booking) {
        RoomTypes roomType = roomTypesMapper.selectById1(booking.getRoomTypeId());
        if (roomType != null && roomType.getAvailableRooms() > 0) {
            // 使用乐观锁更新房间数量
            UpdateWrapper<RoomTypes> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("room_type_id", roomType.getRoomTypeId())
                    .set("available_rooms", roomType.getAvailableRooms() - Integer.parseInt(booking.getRoomNumber()))
                    .set("version", roomType.getVersion() + 1)
                    .eq("version", roomType.getVersion());

            boolean updated = roomTypesMapper.update(roomType, updateWrapper) > 0;
            if (updated) {
                bookingsMapper.insert(booking);
                return Result.ok(booking);
            } else {
                // 更新失败
                return Result.build("房间已被预订，请重试");
            }
        } else {
            return Result.build("库存不足");
        }
    }
}




