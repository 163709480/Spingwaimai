package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entry.Category;
import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired(required = false)
    private DishService dishService;
    @Autowired(required = false)
    private SetmealService setmeal;


    /**
     * 根据id删除分类，删除之前需要判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish>dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);

        log.info("ids打印数值为 = {} ,count数值等于 = {}",ids,count);


        //查询当前分类是否关联菜品，如果关联，抛出一个业务异常
        if(count>0){
            //已经关联菜品

            throw  new CustomException("当前分类下关联菜品,不能删除");


        }

        //查询当前分类是否关联套餐，如果关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal>lqw = new LambdaQueryWrapper<>();

        lqw.eq(Setmeal::getCategoryId,ids);
        int count1 = setmeal.count(lqw);
        if(count1>0){
            //已经关联套餐
            throw  new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(ids);
    }
//    @Override
//    public void remo(Long ids){
//        LambdaQueryWrapper<Dish> dishLqw= new LambdaQueryWrapper<>();
//        dishLqw.eq(Dish::getCategoryId,ids);
//        int count = dishService.count(dishLqw);
//        if(count>0){
//            //有相同
//            throw new CustomException("异常");
//        }
//        LambdaQueryWrapper<Setmeal>setlqw= new LambdaQueryWrapper<>();
//        setlqw.eq(Setmeal::getCategoryId,ids);
//        int count1 = setmeal.count(setlqw);
//        if(count1>0){
//
//        }
//    }
}
