package com.itheima.reggie.dto;

import com.itheima.reggie.entry.Setmeal;
import com.itheima.reggie.entry.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
