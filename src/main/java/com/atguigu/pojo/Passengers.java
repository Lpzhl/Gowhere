package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName passengers
 */
@TableName(value ="passengers")
@Data
public class Passengers implements Serializable {
    @TableId
    private Integer passengerId;

    private Integer bookingId;

    private Integer userId;

    private String name;

    private String identityCardNumber;

    private String phoneNumber;

    private static final long serialVersionUID = 1L;
}