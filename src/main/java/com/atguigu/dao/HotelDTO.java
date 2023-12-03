package com.atguigu.dao;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelDTO {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String destination;
    private String keyword;
}
