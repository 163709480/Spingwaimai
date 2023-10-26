package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.Service.OrdersService;
import com.itheima.reggie.Service.ShoppingCartService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entry.AddressBook;
import com.itheima.reggie.entry.Orders;
import com.itheima.reggie.entry.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>>shoppingCartR(ShoppingCart shoppingCart){


        Long cruuentId = BaseContext.getCruuentId();
        log.info("cruuentID用户id为 = {}",cruuentId);
        LambdaQueryWrapper<ShoppingCart>lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,cruuentId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);

        return R.success(list);
    }

    /**
     * 下单添加到购物车
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart>addShopping(@RequestBody ShoppingCart shoppingCarts){
        List<ShoppingCart>shop= new ArrayList<>();
        //设置用户id，指定当前是哪个用户的购物车数据
        Long cruuentId = BaseContext.getCruuentId();
        shoppingCarts.setUserId(cruuentId);
        LambdaQueryWrapper<ShoppingCart>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,cruuentId);
        //查询当前菜品或者套餐是否再购物车中
        Long dishId = shoppingCarts.getDishId();
        if(dishId!=null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);



        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCarts.getSetmealId());

        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one!=null){
            //原来基础加1
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);

        }else {
            shoppingCarts.setNumber(1);
            shoppingCartService.save(shoppingCarts);
            one=shoppingCarts;

        }







        return R.success(one);
    }


    /**
     * 删减购物车
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart)
    {
        //如果
        LambdaQueryWrapper<ShoppingCart>addressBook= new LambdaQueryWrapper<>();
        addressBook.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
//        shoppingCartService.remove(addressBook);

        ShoppingCart shop = shoppingCartService.getOne(addressBook);
        log.info("获取到的 shop值为 = {}",shop);
        if(shop.getNumber()>0){
            Integer number = shop.getNumber();
            int i = number - 1;
            shop.setNumber(i);
            shoppingCartService.updateById(shop);
            return R.success("删除成功");

        }else if(shop.getNumber()==0){
          shoppingCartService.removeById(shop);

        }


        return R.success("删除成功");



    }

    /**
     * 清除购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String>deleteShopping(ShoppingCart shoppingCart){
        //获取当前用户id
        Long cruuentId = BaseContext.getCruuentId();
        shoppingCart.setUserId(cruuentId);


        LambdaQueryWrapper<ShoppingCart>addShop= new LambdaQueryWrapper<>();
        //创建删除条件
        addShop.eq(cruuentId!=null,ShoppingCart::getUserId,cruuentId);
        //删除代码
        shoppingCartService.remove(addShop);



        return R.success("清楚成功");
    }





}
