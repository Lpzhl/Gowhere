package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName contact_people
 */
@TableName(value ="contact_people")
@Data
public class ContactPeople implements Serializable {
    @TableId
    private Integer contactId;

    private Integer userId;

    private String name;

    private String phoneNumber;

    private String identityCardNumber;

    private static final long serialVersionUID = 1L;
}