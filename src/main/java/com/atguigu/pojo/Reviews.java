package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName reviews
 */
@TableName(value ="reviews")
@Data
public class Reviews implements Serializable {
    @TableId
    private Integer reviewId;

    private Integer hotelId;

    private Integer userId;

    private Integer flightId;

    private BigDecimal rating;

    private String comment;

    private Date reviewDate;

    private Integer bookingId;

    private Integer parentReviewId;

    private static final long serialVersionUID = 1L;
}