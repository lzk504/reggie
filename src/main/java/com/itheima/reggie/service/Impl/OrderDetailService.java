package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId);

    public void updateOrderDetailById(Long id,long orderId);
}
