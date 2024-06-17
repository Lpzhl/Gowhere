package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName review_images
 */
@TableName(value ="review_images")
@Data
public class ReviewImages implements Serializable {
    @TableId
    private Integer imageId;

    private Integer reviewId;

    private String imageUrl;

    private Date uploadedAt;

    private static final long serialVersionUID = 1L;
}