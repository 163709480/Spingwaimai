package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entry.Setmeal;
import org.springframework.stereotype.Service;



public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);
    public SetmealDto getByWithDish(String  categoryId);

}
