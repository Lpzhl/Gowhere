package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName roles
 */
@TableName(value ="roles")
@Data
public class Roles implements Serializable {
    @TableId
    private Integer id;

    private String name;

    private String description;

    private static final long serialVersionUID = 1L;
}