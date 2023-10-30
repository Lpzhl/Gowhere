package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users implements Serializable {
    private Integer id;

    private String nickname;

    private String email;

    private String password;

    private Object avatar;

    private Date registrationdate;

    private static final long serialVersionUID = 1L;
}