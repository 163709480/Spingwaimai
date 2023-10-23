package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.ShoppingCartService;
import com.itheima.reggie.entry.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {


}
