package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName flights
 */
@TableName(value ="flights")
@Data
public class Flights implements Serializable {
    @TableId
    private Integer flightId;

    private Integer airlineId;

    private String flightNumber;

    private String captainName;

    private String airplaneImageUrl;

    private String departureAirport;

    private String arrivalAirport;

    private Date departureTime;

    private Date arrivalTime;

    private BigDecimal price;

    private String status;

    private Integer aircraftId;

    private Integer firstClassSeats;    // 头等舱数量

    private BigDecimal firstClassPrice;  // 头等舱价格

    private Integer economyClassSeats;    //经济舱数量

    private BigDecimal economyClassPrice;

    private Integer standardClassSeats;    //普通舱

    private BigDecimal standardClassPrice;

    private static final long serialVersionUID = 1L;
}