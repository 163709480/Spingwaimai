package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import javafx.scene.control.Pagination;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置MP分页插件
 */
@Configuration
public class MybatisPlus {
   @Bean
   public MybatisPlusInterceptor mybatisPlusInterceptor(){
      MybatisPlusInterceptor mpi =  new MybatisPlusInterceptor();
      mpi.addInnerInterceptor(new PaginationInnerInterceptor());
      return mpi;
   }
}
