package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entry.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
