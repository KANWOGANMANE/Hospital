package com.sjq.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjq.yygh.model.order.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    Object saveOrder(String scheduleId, Long patientId);
}
