package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.OrdersService;
import com.itheima.reggie.entry.Orders;
import com.itheima.reggie.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders>implements OrdersService {
}
