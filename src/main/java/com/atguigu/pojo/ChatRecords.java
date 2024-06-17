package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName chat_records
 */
@TableName(value ="chat_records")
@Data
public class ChatRecords implements Serializable {
    private Integer id;

    private Integer senderId;

    private Integer receiverId;

    private Integer hotelId;

    private Integer orderId;

    private String messageContent;

    private Date timestamp;

    private Object status;

    private Object messageType;

    private String name;

    private static final long serialVersionUID = 1L;
}