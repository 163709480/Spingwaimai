package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.EmployeeService;
import com.itheima.reggie.entry.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

@Service
public class EmoloyeeServiceImpl  extends ServiceImpl<EmployeeMapper, Employee> implements  EmployeeService {
}
