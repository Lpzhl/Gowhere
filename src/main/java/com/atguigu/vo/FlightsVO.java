package com.atguigu.vo;

import com.atguigu.pojo.Airlines;
import com.atguigu.pojo.Flights;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FlightsVO {
    private static int currentId = 0; // 静态变量，用于跟踪下一个 ID

    private int id;
    private Flights1 flights;  // 航班信息
    private BigDecimal price;  // 价格
    private Airlines airlines;   // 航班所属的航空公司


    // 构造函数或者一个初始化方法
    public FlightsVO() {
        this.id = getNextId(); // 设置ID
    }

    // 静态方法，用于获取下一个可用的 ID
    private static synchronized int getNextId() {
        return ++currentId;
    }
}

