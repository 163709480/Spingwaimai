package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.entry.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import org.springframework.stereotype.Service;

@Service

public class DishFloverServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>implements DishFlavorService {
}
