package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.Service.OrdersService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entry.AddressBook;
import com.itheima.reggie.entry.Orders;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController

@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AddressBookService addressBookService;
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Orders> page1= new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw= new LambdaQueryWrapper<>();
        lqw.orderByDesc(Orders::getOrderTime);
        ordersService.page(page1,lqw);
        return R.success(page1);

    }

    @GetMapping("/userPage")
    public R<Page>disPage(int page,int pageSize){
        Page<OrdersDto>ordersDtoPage = new Page<>();
        Page<Orders>ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders>lqw= new LambdaQueryWrapper<>();
        lqw.orderByAsc(Orders::getOrderTime);
        Page<Orders> page1 = ordersService.page(ordersPage, lqw);
//
//
        return R.success(ordersPage);

    }

    /**
     * 提交订单信息到数据库
     * @return
     */
    @PostMapping("/submit")
    public R<String>sumbitShopping(@RequestBody Orders orders){
        Long cruuentId = BaseContext.getCruuentId();
        orders.setUserId(cruuentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(BigDecimal.valueOf(199));
        ordersService.save(orders);
       return R.success("添加成功");



    }

}
