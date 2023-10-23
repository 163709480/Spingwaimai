package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entry.Setmeal;
import com.itheima.reggie.entry.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 保存套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
       //保存套餐基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息,setmeal_dish
        setmealDishService.saveBatch(setmealDishes);



    }

    @Override
    public SetmealDto getByWithDish(String categoryId) {
        LambdaQueryWrapper<SetmealDish> lqw= new LambdaQueryWrapper<>();
        SetmealDto set= new SetmealDto();
        Setmeal byId = this.getById(categoryId);
        BeanUtils.copyProperties(byId,set);
        lqw.eq(SetmealDish::getSetmealId,byId.getId());
        List<SetmealDish> list = setmealDishService.list(lqw);
        set.setSetmealDishes(list);


        return set;
    }
}
