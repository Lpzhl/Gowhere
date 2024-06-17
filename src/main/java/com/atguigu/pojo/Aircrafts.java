package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @TableName aircrafts
 */
@TableName(value ="aircrafts")
@Data
public class Aircrafts implements Serializable {
    @TableId
    private Integer aircraftId;

    private String model;

    private String manufacturer;

    private Integer capacity;

    private Integer rangeKm;

    private BigDecimal wingspanM;

    private BigDecimal lengthM;

    private BigDecimal heightM;

    private Integer cruiseSpeedKmh;

    private static final long serialVersionUID = 1L;
}