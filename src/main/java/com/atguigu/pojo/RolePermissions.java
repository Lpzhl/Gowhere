package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName role_permissions
 */
@TableName(value ="role_permissions")
@Data
public class RolePermissions implements Serializable {
    @TableId
    private Integer roleId;

    private Integer permissionId;

    private static final long serialVersionUID = 1L;
}