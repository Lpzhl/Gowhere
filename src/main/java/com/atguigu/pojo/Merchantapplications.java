package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName merchantapplications
 */
@TableName(value ="merchantapplications")
@Data
public class Merchantapplications implements Serializable {
    private Integer id;

    private Integer applicantid;

    private String name;

    private String applicationimage;

    private String contactphone;

    private String identitycardnumber;

    private String status;

    private static final long serialVersionUID = 1L;
}