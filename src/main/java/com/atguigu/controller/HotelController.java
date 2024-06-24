package com.atguigu.controller;


import com.atguigu.dao.HotelDTO;
import com.atguigu.mapper.HotelImagesMapper;
import com.atguigu.mapper.RoomTypesMapper;
import com.atguigu.pojo.*;
import com.atguigu.service.BookingsService;
import com.atguigu.service.HotelImagesService;
import com.atguigu.service.HotelsService;
import com.atguigu.service.RoomTypesService;
import com.atguigu.utils.AliOssUtil;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.atguigu.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hotel")
@CrossOrigin
public class HotelController {

    @Autowired
    private HotelsService hotelService;

    @Autowired
    private HotelImagesService hotelImagesService;

    @Autowired
    private HotelImagesMapper hotelImagesMapper;

    @Autowired
    private RoomTypesService roomTypesService;

    @Autowired
    private RoomTypesMapper roomTypesMapper;

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private AliOssUtil aliOssUtil;

  /*  private String uploadDirectory = "D:/xuniResoure/"; // 指定图片上传的目录*/
    private final String uploadDirectory = "C:\\Users\\LoveF\\Desktop/xuniResoure/"; // 指定图片上传的目录

    /**
     * 前十的热门酒店
     *
     */
    @GetMapping("/getHotelsByHot")
    public Result getHotelsByHot(){
        List<HotelsVO> hotels = hotelService.getHotelsByHot();
        return Result.ok(hotels);
    }

    /**
     * 根据酒店id获取酒店信息
     * @param hotelId
     * @return
     */
    @GetMapping("/getHotelNameById")
    public Result getHotelNameById (@RequestParam int hotelId){
        String hotelNameById = hotelService.getHotelNameById(hotelId);
        return  Result.ok(hotelNameById);
    }

    @GetMapping("/getHotelById")
    public Result getHotelById (@RequestParam int hotelId){
        Hotels hotels = hotelService.getHotelById(hotelId);
        return  Result.ok(hotels);
    }


    //根据先是根据用户id获取酒店的id，再根据酒店id获取订单信息
    @GetMapping("/getHotelIdAndBookingsByUserId")
    public Result getHotelIdAndBookingsByUserId(@RequestParam int userId) {
        try {
            System.out.println("userId = " + userId);
            List<Integer> hotelIds = hotelService.getHotelIdByUserId(userId);

            if (hotelIds.isEmpty()) {
                return Result.build("失败");
            }

            Map<Integer, List<Bookings>> allBookings = new HashMap<>();
            for (int hotelId : hotelIds) {
                List<Bookings> bookings = bookingsService.getBookingsByHotelId(hotelId);
                allBookings.put(hotelId, bookings);
            }

            System.out.println("Bookings = " + allBookings);

            return Result.ok(allBookings);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build("失败");
        }
    }



    @PostMapping("/deleteRoomImage")
    public Result deleteRoomImage (@RequestParam int imageId){
        System.out.println("image8898Id = " + imageId);
        try {
            QueryWrapper<HotelImages> hotelImagesQueryWrapper = new QueryWrapper<>();
            hotelImagesQueryWrapper.eq("image_id",imageId);
            int delete = hotelImagesMapper.delete(hotelImagesQueryWrapper);

            System.out.println("delete = " + delete);
            return Result.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return Result.build("删除失败");
        }
    }
    /**
     * 获取房间信息
     */
    @GetMapping("/getRoomInfo")
    public Result getRoomsByHotelId(@RequestParam int hotelId) {

        try {

            QueryWrapper<RoomTypes> roomQueryWrapper = new QueryWrapper<>();
            roomQueryWrapper.eq("hotel_id", hotelId);
            List<RoomTypes> roomTypesList = roomTypesMapper.selectList(roomQueryWrapper);
            System.out.println("roomTypesList = " + roomTypesList);

            List<RoomTypeVO> roomTypeVOs = roomTypesList.stream()
                    .map(this::convertToRoomTypeVO)
                    .collect(Collectors.toList());

            return Result.ok(roomTypeVOs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build("获取失败");
        }
    }
    /**
     * 获取指定酒店的所有房间信息
     */


    /**
     * 删除酒店房间
     */
    @PostMapping("/deleteRoom")
    public Result deleteRoom(@RequestParam int roomId) {
        System.out.println("roomId = " + roomId);
        try {
            roomTypesService.deleteRoomType(roomId);
            return Result.ok("房间删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build("删除失败");
        }
    }

    /**
     * 添加酒店房间信息
     */
    @PostMapping("/updateRoom")
    public Result addRoomTypeImages(@RequestBody RoomTypes roomType) {
        System.out.println("HHHHHH = " + roomType);
        try {
            System.out.println("房间修改");
            boolean b = roomTypesService.updateRoomType(roomType);
            System.out.println("b = " + b);
            if(b){
                return Result.ok("房间修改成功");
            }
        } catch (Exception e) {
                e.printStackTrace();
                return Result.build("房间修改失败");
            }
        return Result.build("房间修改失败");
    }

    /**
     * 添加酒店房间信息
     */
    @PostMapping("/addRoom")
    public Result addRoomType(@RequestBody RoomTypes roomType) {
        System.out.println("roomType = " + roomType);
        try {
            // 生成唯一的4位数SKU
            String uniqueSku = generateUniqueSku();
            roomType.setSku(uniqueSku);

            roomTypesService.addRoomType(roomType);
            return Result.ok("房间类型添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build("添加失败");
        }
    }
    private String generateUniqueSku() {
        Random random = new Random();
        String sku;
        boolean exists;
        // 生成随机4位数
        sku = String.format("RT%04d", random.nextInt(10000));
        return sku;
    }

    /***
     * 下架酒店
     * @param hotelId
     * @return
     */
    @PostMapping("/delete")
    public Result off(@RequestParam int hotelId) {
        boolean success = hotelService.disableMerchant(hotelId);
        if (success) {
            return Result.ok("酒店已被禁用");
        } else {
            return Result.build("操作失败，找不到指定的酒店");
        }
    }


    /**
     * 删除照片
     * @param imageId
     * @param hotelId
     * @return
     */
    @PostMapping("/deleteImage")
    public Result deleteHotelImages(@RequestParam int imageId, @RequestParam int hotelId) {
        boolean success = hotelImagesService.removeHotelImage(imageId, hotelId);
        if (success) {
            return Result.ok("图片删除成功");
        } else {
            return Result.build("图片删除失败");
        }
    }
    @PostMapping("/uploadRoom")
    public Result uploadRoomImages(@RequestParam("file") MultipartFile file,
                                   @RequestParam("roomType_id") int roomTypeId) {

        if (file.isEmpty()) {
            return Result.build("文件为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            String fileUrl = aliOssUtil.upload(file.getBytes(), objectName);

            HotelImages hotelImages = new HotelImages();
            hotelImages.setRoomTypeId(roomTypeId);
            hotelImages.setImageUrl(fileUrl);

            hotelImagesService.save(hotelImages);

            return Result.ok(fileUrl);
        } catch (IOException e) {
            return Result.build("文件上传失败");
        }
    }

    @PostMapping("/upload")
    public Result uploadReviewImages(@RequestParam("file") MultipartFile file,
                                     @RequestParam("hotel_id") int hotelId) {

        if (file.isEmpty()) {
            return Result.build("文件为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            String fileUrl = aliOssUtil.upload(file.getBytes(), objectName);

            HotelImages hotelImages = new HotelImages();
            hotelImages.setHotelId(hotelId);
            hotelImages.setImageUrl(fileUrl);

            hotelImagesService.save(hotelImages);

            return Result.ok(fileUrl);
        } catch (IOException e) {
            return Result.build("文件上传失败");
        }
    }

/*
    @PostMapping("/uploadRoom")
    public Result uploadRoomImages(@RequestParam("file") MultipartFile file,
                                     @RequestParam("roomType_id") int roomTypeId)  throws IOException {
        System.out.println("file = " + file);
        System.out.println("roomTypeId = " + roomTypeId);
        if (file.isEmpty()) {
            return Result.build("文件为空");
        }

        // 生成文件在服务器上的唯一名字
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename;
        System.out.println("fileName = " + fileName);
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        //E:\java\xuniResoure\屏幕截图(15).png
        System.out.println("filePath = " + filePath);
        String  fliename = "http://localhost:8088/resources/"+fileName;
        try {
            // 保存文件到服务器
            File dest = new File(filePath);
            file.transferTo(dest);

            // 将文件信息保存到数据库
            HotelImages hotelImages = new HotelImages();
            hotelImages.setRoomTypeId(roomTypeId);
            hotelImages.setImageUrl(fliename);

            hotelImagesService.save(hotelImages);


            return Result.ok(fliename);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.build("文件上传失败");
        }
    }

    */
/**
     * 上传酒店照片
     * @param file
     * @param hotelId
     * @return
     * @throws IOException
     *//*

    @PostMapping("/upload")
    public Result uploadReviewImages(@RequestParam("file") MultipartFile file,
                                     @RequestParam("hotel_id") int hotelId)  throws IOException {
        System.out.println("file = " + file);
        System.out.println("hotelId = " + hotelId);
        if (file.isEmpty()) {
            return Result.build("文件为空");
        }

        // 生成文件在服务器上的唯一名字
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename;
        System.out.println("fileName = " + fileName);
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        //E:\java\xuniResoure\屏幕截图(15).png
        System.out.println("filePath = " + filePath);
        String  fliename = "http://localhost:8088/resources/"+fileName;
        try {
            // 保存文件到服务器
            File dest = new File(filePath);
            file.transferTo(dest);

            // 将文件信息保存到数据库
            HotelImages hotelImages = new HotelImages();
            hotelImages.setHotelId(hotelId);
            hotelImages.setImageUrl(fliename);

            hotelImagesService.save(hotelImages);


            return Result.ok(fliename);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.build("文件上传失败");
        }
    }
*/


    /**
     * 添加酒店
     * @param hotel
     * @return
     */
    @PostMapping("/addHotel")
    public Result addHotel(@RequestBody Hotels hotel) {
        System.out.println("hotel = " + hotel);
        Hotels updatedHotel = hotelService.saveOrUpdateHotel(hotel);
        if (updatedHotel != null) {
            return Result.ok( updatedHotel);
        } else {
            return Result.build("操作失败");
        }
    }

    /**
     * 获取所有酒店信息
     * @param ownerId
     * @return
     */

    @GetMapping("/getHotels")
    public Result getHotelByOwnerId(@RequestParam int ownerId){
        System.out.println("ownerId = " + ownerId);
        try {
            List<HotelVO2> hotelInfo = hotelService.getHotelsByOwnerId(ownerId);
            return Result.ok(hotelInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
    }

    /**
     * 根据hotelId获取酒店信息
     */

    @GetMapping("/hotelId")
    public Result getHotelInfo(@RequestParam int hotelId) {
        System.out.println("hotelId = " + hotelId);
        try {
            List<HotelVO> hotelInfo = hotelService.getHotelsByHotelId(hotelId);
            return Result.ok(hotelInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
    }



    /**
     * 酒店搜索
     * @param hotelDTO
     * @return
     */
    @PostMapping("/search")
    public Result<List<HotelVO2>> searchHotels(@RequestBody HotelDTO hotelDTO) {
        System.out.println("hotelDTO = " + hotelDTO);
        List<HotelVO2> hotels = hotelService.searchHotels(hotelDTO);
        System.out.println("hotels864 = " + hotels);
        return Result.ok(hotels);
    }


    /**
     * 刷新酒店详情界面
     *
     */
    @PostMapping("/update")
    public Result<List<HotelVO>> updateHotels(@RequestParam int hotelId){
        //System.out.println("hotelId = " + hotelId);

        List<HotelVO> hotels = hotelService.getHotelsByHotelId(hotelId);
        System.out.println("通过id搜索hotels = " + hotels);
        return Result.ok(hotels);
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

}
