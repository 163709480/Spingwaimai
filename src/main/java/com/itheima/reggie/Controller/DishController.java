package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entry.Category;
import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.DishFlavor;
import com.itheima.reggie.entry.Setmeal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j


public class DishController {
    /**
     * 分页显示
     */
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    @Autowired

    private SetmealService setmealService;
    @Autowired
    private RedisTemplate redisTemplate;



    @GetMapping("/page")
    public R<Page>displayPage(int page,int pageSize,String name){
//
        LambdaQueryWrapper<Dish>lqw =new LambdaQueryWrapper<>();

        Page<Dish>dishPage = new Page<>(page,pageSize);
        Page<DishDto>dishDtoPage = new Page<>();
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        dishService.page(dishPage,lqw);
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;


        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }



    /**
     * 删除菜品
     * @param  ids 前端传来的菜品数据
     * @return
     */
    @DeleteMapping
    public R<String>DeleteRows(String[] ids){
        dishService.removeByIds(Arrays.asList(ids));


        return R.success("删除成功");
    }


    /**
     * 单个和批量停售
     * @param status status对应数据表中该字段 0为停售状态 1为起售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String>NOPAY(@PathVariable int status,String[] ids){
//        boolean b = dishService.getById(ids);
        for (String id : ids) {
            //获取id是否存在数据库中
            Dish byId = dishService.getById(id);
            byId.setStatus(status);
            dishService.updateById(byId);


        }
            log.info("ids数值为 = {}",status);



        return R.success("停售成功");
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String>AddDishFlover(@RequestBody DishDto dishDto){
//        Long emoloyee =(Long) request.getSession().getAttribute("emoloyee");
        log.info("dishDto的值为 = {}",dishDto);
//        dishFlavor.setDishId(emoloyee);
            dishService.saveWeithFlover(dishDto);
        //清理所有缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

//        dishService.save(dishDto);
       return   R.success("添加菜品成功");
    }


    @GetMapping("/list")

    public R<List<DishDto>>categoryR(String categoryId,String status){
        List<DishDto>dishDtoList = null;

        //通过菜品id和status状态来查询数据
       // 动态获取Key
        String keys ="dish_"+categoryId+"_"+status;
      //在redis里面取keys // 反序列化
       dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(keys);


        //如果keys能获取到数据 则直接return
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }


        //如果获取不到数据则执行查询语句



        LambdaQueryWrapper<Dish>lqw= new LambdaQueryWrapper<>();
        lqw.eq(categoryId!=null,Dish::getCategoryId,categoryId);
//        //查询状态为1
       lqw.eq(Dish::getStatus,1);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);

       dishDtoList= list.stream().map((item)->{
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(item,dishDto);

        //获取当前菜品Id
           Long dishId = item.getId();
           LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
           queryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL select * from dish)falove where dish_id = ?
           List<DishFlavor> list1 = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(list1);

            return dishDto;

       }).collect(Collectors.toList());
       //查询完成后的数据存储到redis当中
        redisTemplate.opsForValue().set(keys,dishDtoList,60,TimeUnit.MINUTES);

        System.out.println("程序结束");
        return R.success(dishDtoList);

    }
    /**
     * 修改数据
     * @return
     */
    @PutMapping
    public R<String>UpdateDate(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);


    //清理所有缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        //精确清理  清理某个分类下面缓存
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("更新成功");

    }




    @GetMapping("/{id}")
    public R<DishDto>DIStO(@PathVariable String id){
        LambdaQueryWrapper<DishFlavor>dish= new LambdaQueryWrapper<>();
        DishDto dishDto = new DishDto();




        Dish byId = dishService.getById(id);
        BeanUtils.copyProperties(byId,dishDto);
        dish.eq(DishFlavor::getDishId,byId.getId());
        List<DishFlavor> list = dishFlavorService.list(dish);
        dishDto.setFlavors(list);
//        Category byId1 = categoryService.getById(byId.getCategoryId());
//        dishDto.setCategoryName(byId1.getName());


        log.info("Dish数据为 = {}",byId);
//        DishFlavor byId1 = dishFlavorService.getById(id);
        return R.success(dishDto);

    }
//    @GetMapping
//    public R<OrdersDto>add(String type){
//        return null;
//    }


}
