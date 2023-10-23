package com.itheima.reggie.dto;

import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.DishFlavor;
import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
