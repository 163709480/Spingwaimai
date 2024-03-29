package com.itheima.reggie.dto;

import com.itheima.reggie.entry.OrderDetail;
import com.itheima.reggie.entry.Orders;
import com.itheima.reggie.entry.OrderDetail;
import com.itheima.reggie.entry.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
