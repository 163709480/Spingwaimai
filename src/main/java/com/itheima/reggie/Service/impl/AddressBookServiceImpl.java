package com.itheima.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.entry.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
