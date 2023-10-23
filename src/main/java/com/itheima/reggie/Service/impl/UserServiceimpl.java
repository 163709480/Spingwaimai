package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.UserService;
import com.itheima.reggie.entry.User;
import com.itheima.reggie.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User>  implements UserService {
}
