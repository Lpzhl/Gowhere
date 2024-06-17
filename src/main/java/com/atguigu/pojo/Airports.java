package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @TableName airports
 */
@TableName(value ="airports")
@Data
public class Airports implements Serializable {
    @TableId
    private Integer airportId;

    private String name;

    private String city;

    private String country;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String timezone;

    private static final long serialVersionUID = 1L;
}