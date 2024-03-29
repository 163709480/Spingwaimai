package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entry.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWeithFlover(DishDto dishDto);
    public DishDto getByidWithFlacor(String id);
//更新菜品信息，同时更新对应的口味信息
   public void updateWithFlavor(DishDto dishDto);
}
