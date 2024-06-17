package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName airlines
 */
@TableName(value ="airlines")
@Data
public class Airlines implements Serializable {
    @TableId
    private Integer airlineId;

    private String name;

    private String headquarters;

    private Date foundedDate;

    private String photoUrl;

    private static final long serialVersionUID = 1L;
}