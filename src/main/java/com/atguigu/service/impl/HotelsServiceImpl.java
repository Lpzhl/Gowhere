package com.atguigu.service.impl;

import com.atguigu.dao.HotelDTO;
import com.atguigu.mapper.HotelImagesMapper;
import com.atguigu.mapper.RoomTypesMapper;
import com.atguigu.pojo.HotelImages;
import com.atguigu.pojo.RoomTypes;
import com.atguigu.vo.HotelImageVO;
import com.atguigu.vo.HotelVO;
import com.atguigu.vo.RoomTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Hotels;
import com.atguigu.service.HotelsService;
import com.atguigu.mapper.HotelsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author LoveF
* @description 针对表【hotels】的数据库操作Service实现
* @createDate 2023-11-26 15:11:43
*/
@Service
public class HotelsServiceImpl extends ServiceImpl<HotelsMapper, Hotels>
        implements HotelsService {

    @Autowired
    private RoomTypesMapper roomTypesMapper;
    @Autowired
    private HotelImagesMapper hotelImagesMapper;

    @Override
    public List<HotelVO> searchHotels(HotelDTO hotelDTO) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", hotelDTO.getDestination())
                .or()
                .like("transport", hotelDTO.getDestination());

        List<Hotels> hotels = list(queryWrapper);
        return hotels.stream().map(this::convertToHotelVO).collect(Collectors.toList());
    }

    @Override
    public List<HotelVO> getHotelsByHotelId(int hotelId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id", hotelId);

        List<Hotels> hotels = list(queryWrapper);
        return hotels.stream().map(this::convertToHotelVO).collect(Collectors.toList());
    }


    private HotelVO convertToHotelVO(Hotels hotel) {
        HotelVO hotelVO = new HotelVO();

        hotelVO.setHotelId(hotel.getHotelId());
        hotelVO.setName(hotel.getName());
        hotelVO.setAddress(hotel.getAddress());
        hotelVO.setPhoneNumber(hotel.getPhoneNumber());
        hotelVO.setDescription(hotel.getDescription());
        hotelVO.setRating(hotel.getRating());
        hotelVO.setActive(hotel.getActive());
        hotelVO.setAvailableRooms(hotel.getAvailableRooms());
        hotelVO.setTransport(hotel.getTransport());
        hotelVO.setTag(hotel.getTag());
        hotelVO.setPrice(hotel.getPrice());


        QueryWrapper<RoomTypes> roomQueryWrapper = new QueryWrapper<>();
        roomQueryWrapper.eq("hotel_id", hotel.getHotelId());
        List<RoomTypes> roomTypes = roomTypesMapper.selectList(roomQueryWrapper);
        List<RoomTypeVO> roomTypeVOs = roomTypes.stream().map(this::convertToRoomTypeVO).collect(Collectors.toList());
        hotelVO.setRoomTypes(roomTypeVOs);


        QueryWrapper<HotelImages> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("hotel_id", hotel.getHotelId());
        List<HotelImages> hotelImages = hotelImagesMapper.selectList(imageQueryWrapper);
        List<HotelImageVO> hotelImageVOs = hotelImages.stream().map(this::convertToHotelImageVO).collect(Collectors.toList());
        System.out.println("hotelImageVOs = " + hotelImageVOs);
        hotelVO.setImages(hotelImageVOs);

        return hotelVO;
    }

    private RoomTypeVO convertToRoomTypeVO(RoomTypes roomType) {
        RoomTypeVO roomTypeVO = new RoomTypeVO();

        roomTypeVO.setRoomTypeId(roomType.getRoomTypeId());
        roomTypeVO.setTypeName(roomType.getName());
        roomTypeVO.setDescription(roomType.getDescription());
        roomTypeVO.setOriginalPrice(roomType.getOriginalprice());
        roomTypeVO.setDiscountPrice(roomType.getDiscountprice());
        roomTypeVO.setAvailableRooms(roomType.getAvailableRooms());
        roomTypeVO.setName(roomType.getName());
        roomTypeVO.setBedType(roomType.getBedtype());
        roomTypeVO.setBreakfast(roomType.getBreakfast());
        roomTypeVO.setHotelId(roomType.getRoomTypeId());
        roomTypeVO.setVersion(roomType.getVersion());

        // 加载与此房间类型相关的图片
        QueryWrapper<HotelImages> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("room_type_id", roomType.getRoomTypeId());
        List<HotelImages> roomImages = hotelImagesMapper.selectList(imageQueryWrapper);
        List<HotelImageVO> roomImageVOs = roomImages.stream().map(this::convertToHotelImageVO).collect(Collectors.toList());
        System.out.println("roomImageVOs = " + roomImageVOs);
        roomTypeVO.setImages(roomImageVOs);

        return roomTypeVO;
    }

    private HotelImageVO convertToHotelImageVO(HotelImages hotelImage) {
        HotelImageVO hotelImageVO = new HotelImageVO();

        hotelImageVO.setImageId(hotelImage.getImageId());
        hotelImageVO.setHotelId(hotelImage.getHotelId());
        hotelImageVO.setRoomTypeId(hotelImage.getRoomTypeId());
        hotelImageVO.setDescription(hotelImage.getDescription());
        hotelImageVO.setImageUrl(hotelImage.getImageUrl());
        hotelImageVO.setImageType(hotelImage.getImageType());

        return hotelImageVO;
    }
}





