package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entry.Category;
import com.itheima.reggie.entry.Dish;
import com.itheima.reggie.entry.Setmeal;
import com.itheima.reggie.entry.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")

public class SetmealController {
    @Autowired

    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> disPage(int page,int pageSize){
        Page<Setmeal> dishpage= new Page<>(page,pageSize);
        Page<SetmealDto> ObSetmeal = new Page<>();


        LambdaQueryWrapper<Setmeal>lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Setmeal::getUpdateTime);

        setmealService.page(dishpage,lqw);



        BeanUtils.copyProperties(dishpage,ObSetmeal,"orderDetails");
        List<Setmeal> records = dishpage.getRecords();
       List<SetmealDto> set= records.stream().map((item)->{
            SetmealDto smd= new SetmealDto();
            BeanUtils.copyProperties(item,smd);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            String name = byId.getName();
            smd.setCategoryName(name);

            return smd;

        }).collect(Collectors.toList());


       ObSetmeal.setRecords(set);



        return R.success(ObSetmeal);

    }

    /**
     * 停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String>HaltShop(@PathVariable int status, String[] ids){
        log.info("status的值为 = {}",status);


        for (String id : ids) {
            Setmeal byId = setmealService.getById(id);
            byId.setStatus(status);
            setmealService.updateById(byId);
        }


        return R.success("停售成功");
    }

    @GetMapping("/{categoryId}")
    public R<SetmealDto>Adddisplay(@PathVariable String categoryId){
//        LambdaQueryWrapper<SetmealDish>lqw= new LambdaQueryWrapper<>();
//        SetmealDto setmealDto= new SetmealDto();
//
//        Setmeal setmeal = setmealService.getById(categoryId);
//        BeanUtils.copyProperties(setmeal,setmealDto);
//
//        lqw.eq(SetmealDish::getSetmealId,setmeal.getCategoryId());
//        List<SetmealDish> list = setmealDishService.list(lqw);
//        setmealDto.setSetmealDishes(list);
        SetmealDto byWithDish = setmealService.getByWithDish(categoryId);



        return R.success(byWithDish);
    }
    @Cacheable(value = "dishlist",key = "#categoryId+'_'+#status")
    @GetMapping("/list")
    public R<List<Setmeal>>SetmealQuerry(String categoryId,int status){
        List<Setmeal> list=null;
        //动态key
        String keys="dish_"+categoryId+"_"+status;
        Object o = redisTemplate.opsForValue().get(keys);
//        log.info("o.to = {}",o.toString());
        list = (List<Setmeal>) redisTemplate.opsForValue().get(keys);

         if(list!=null){
             return R.success(list);
         }


        LambdaQueryWrapper<Setmeal>lqwsetmeal = new LambdaQueryWrapper<>();
        lqwsetmeal.eq(categoryId!=null,Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,status);
       list = setmealService.list(lqwsetmeal);
       //第一个数据必须设置keys值
       redisTemplate.opsForValue().set(keys,list,60, TimeUnit.MINUTES);


        return R.success(list);
    }

    @CacheEvict(value = "dishlist",allEntries = true)
    @PostMapping
    public R<String>addSort(@RequestBody SetmealDto setmealDto){
      setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @CacheEvict(value = "dishlist",allEntries = true)
    @DeleteMapping
    public R<String>delete(String[] ids){

        LambdaQueryWrapper<SetmealDish> lqw= new LambdaQueryWrapper<>();

        for (String id : ids) {
            LambdaQueryWrapper<Setmeal> lq= new LambdaQueryWrapper<>();
            Setmeal setmeal = setmealService.getById(id);
            lq.eq(setmeal.getStatus()!=1,Setmeal::getId,setmeal.getId());
            if(setmeal.getStatus().equals(1)){
                return R.error("不能删除正在售卖中的套餐");
            }

            log.info("Steaml getid的值为 = {}",setmeal.getId());
            lqw.eq(SetmealDish::getSetmealId,setmeal.getId());
            log.info("lambda表达式 lqw第一次循环与第二次循环的值 ={}",lq.toString());

            setmealService.remove(lq);
            setmealDishService.remove(lqw);
            log.info("传入的id为 = {}",id);


        }












        return R.success("删除成功");
    }

}
