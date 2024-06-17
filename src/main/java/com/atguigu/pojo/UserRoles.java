package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName user_roles
 */
@TableName(value ="user_roles")
@Data
public class UserRoles implements Serializable {
    @TableId
    private Integer userId;

    private Integer roleId;

    private static final long serialVersionUID = 1L;

    public UserRoles(int userId, int roleId) {
        this.roleId = roleId;
        this.userId = userId;
    }
}