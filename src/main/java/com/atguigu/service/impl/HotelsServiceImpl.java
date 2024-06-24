package com.atguigu.service.impl;

import com.atguigu.dao.HotelDTO;
import com.atguigu.mapper.*;
import com.atguigu.pojo.HotelImages;
import com.atguigu.pojo.RoomTypes;
import com.atguigu.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Hotels;
import com.atguigu.service.HotelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
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

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private HotelsMapper hotelsMapper;

    @Autowired
    private BookingsMapper bookingsMapper;

    /**
     * 获取订单数量排行前十的酒店
     *
     * @return
     */
    @Override
    public List<HotelsVO> getHotelsByHot() {
        return hotelsMapper.getTopHotelsByBookings();
    }


    @Override
    public String getHotelNameById(int hotelId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id",hotelId);
        Hotels hotels = hotelsMapper.selectOne(queryWrapper);
        return hotels.getName();
    }

    @Override
    public Hotels getHotelById(int hotelId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id",hotelId);
        return hotelsMapper.selectOne(queryWrapper);
    }



    @Override
    public List<Integer> getHotelIdByUserId(int userId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", userId);
        List<Hotels> hotels = hotelsMapper.selectList(queryWrapper);

        return hotels.stream().map(Hotels::getHotelId).collect(Collectors.toList());
    }


/*        @Override
        public List<i> getHotelIdByUserId(int userId) {
            QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("owner_id", userId);
            return hotelsMapper.selectList(queryWrapper);
        }*/


    @Override
    public Hotels saveOrUpdateHotel(Hotels hotel) {
        if (hotel.getHotelId() != null && this.getById(hotel.getHotelId()) != null) {
            this.updateById(hotel);
        } else {
            this.save(hotel);
        }
        return hotel;
    }

    @Override
    public boolean disableMerchant(int hotelId) {
        // 获取酒店对象
        Hotels hotel = this.getById(hotelId);
        if (hotel != null) {
            if(hotel.getMerchantDisabled()){
                hotel.setMerchantDisabled(false);
            }else {
                hotel.setMerchantDisabled(true);
            }


            // 更新酒店对象
            return this.updateById(hotel);
        }
        // 如果酒店不存在，返回false
        return false;
    }



    @Override
    public List<HotelVO2> searchHotels(HotelDTO hotelDTO) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", hotelDTO.getDestination())
                .or()
                .like("transport", hotelDTO.getDestination());

        List<Hotels> hotels = list(queryWrapper);
        return hotels.stream().map(this::convertToHotelVO2).collect(Collectors.toList());
    }

    @Override
    public List<HotelVO> getHotelsByHotelId(int hotelId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id", hotelId);

        List<Hotels> hotels = list(queryWrapper);
        return hotels.stream().map(this::convertToHotelVO).collect(Collectors.toList());
    }

    @Override
    public List<HotelVO2> getHotelsByOwnerId(int ownerId) {
        QueryWrapper<Hotels> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", ownerId);

        List<Hotels> hotels = list(queryWrapper);
        return hotels.stream().map(this::convertToHotelVO2).collect(Collectors.toList());
    }

    private HotelVO2 convertToHotelVO2(Hotels hotel) {
        System.out.println("777777hotel = " + hotel);

        HotelVO2 hotelVO = new HotelVO2();

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
        hotelVO.setAdminDisabled(hotel.getAdminDisabled());

        hotelVO.setOwnerId(hotel.getOwnerId());
        hotelVO.setMerchantDisabled(hotel.getMerchantDisabled());

        QueryWrapper<RoomTypes> roomQueryWrapper = new QueryWrapper<>();
        roomQueryWrapper.eq("hotel_id", hotel.getHotelId());
        List<RoomTypes> roomTypes = roomTypesMapper.selectList(roomQueryWrapper);
        List<RoomTypeVO> roomTypeVOs = roomTypes.stream().map(this::convertToRoomTypeVO).collect(Collectors.toList());
        hotelVO.setRoomTypes(roomTypeVOs);


        QueryWrapper<HotelImages> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("hotel_id", hotel.getHotelId());
        List<HotelImages> hotelImages = hotelImagesMapper.selectList(imageQueryWrapper);
        List<HotelImageVO2> hotelImageVOs = hotelImages.stream().map(this::convertToHotelImageVO2).collect(Collectors.toList());

        if(hotelImageVOs.size()>0){
            // 创建一个 Random 对象用于生成随机数
            Random random = new Random();

            // 从列表中随机选择一个 HotelImageVO2 对象
            HotelImageVO2 randomHotelImage = hotelImageVOs.get(random.nextInt(hotelImageVOs.size()));

            // 从该对象中获取图片 URL 并设置到 hotelVO 中
            hotelVO.setImageUrl(randomHotelImage.getImageUrl());
        }

        System.out.println("hotelImageVOs = " + hotelImageVOs);
        hotelVO.setImages(hotelImageVOs);

        return hotelVO;
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
        roomTypeVO.setOriginalprice(roomType.getOriginalprice());
        roomTypeVO.setDiscountprice(roomType.getDiscountprice());
        roomTypeVO.setAvailableRooms(roomType.getAvailableRooms());
        roomTypeVO.setName(roomType.getName());
        roomTypeVO.setBedtype(roomType.getBedtype());
        roomTypeVO.setBreakfast(roomType.getBreakfast());
        roomTypeVO.setHotelId(roomType.getHotelId());
        roomTypeVO.setVersion(roomType.getVersion());
        roomTypeVO.setSku(roomType.getSku());

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
    private HotelImageVO2 convertToHotelImageVO2(HotelImages hotelImage) {
        HotelImageVO2 hotelImageVO = new HotelImageVO2();

        hotelImageVO.setImageId(hotelImage.getImageId());
        hotelImageVO.setHotelId(hotelImage.getHotelId());
        hotelImageVO.setRoomTypeId(hotelImage.getRoomTypeId());
        hotelImageVO.setDescription(hotelImage.getDescription());
        hotelImageVO.setImageUrl(hotelImage.getImageUrl());
        hotelImageVO.setImageType(hotelImage.getImageType());

        return hotelImageVO;
    }

}





