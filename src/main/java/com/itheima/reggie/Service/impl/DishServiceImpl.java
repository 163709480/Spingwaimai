package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish>implements DishService {
   @Autowired
   private DishFlavorService dishFlavorService;
   @Autowired
   private  DishService dishService;



    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWeithFlover(DishDto dishDto) {
        //保存菜品基本喜喜
        this.save(dishDto);
        Long dishid = dishDto.getId();//菜品ID
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors= flavors.stream().map((item)->{
            item.setDishId(dishid);
            return item;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表dish flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByidWithFlacor(String id) {
        //查询菜品基本信息 dish
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor>lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lqw);
        dishDto.setFlavors(list);


        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据 dish _flavor
        LambdaQueryWrapper<DishFlavor>querry = new LambdaQueryWrapper<>();
        querry.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(querry);

        //添加当前提交过来的口味数据 dish_flovr插入
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;

        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(flavors);



    }
}
