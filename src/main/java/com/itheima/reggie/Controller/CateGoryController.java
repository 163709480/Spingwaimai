package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entry.Category;
import com.itheima.reggie.entry.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CateGoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页显示
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> display(int page,int pageSize){

        Page Cpage= new Page<>(page,pageSize);
        LambdaQueryWrapper<Category>lqwcate= new LambdaQueryWrapper<>();
        lqwcate.orderByAsc(Category::getSort);
        categoryService.page(Cpage,lqwcate);


        return R.success(Cpage);
    }
    @GetMapping("/list")
    public R<List<Category>>listR(String type,String categoryId){
//    public R<List<Category>>listR( Category category){
        log.info("categoryid的值为 = {}",type);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(type!=null,Category::getType,type);



//        if(category.=="1"){
//            lqw.like(Category::getSort,"1");
//        }else if(type=="2"){
//            lqw.like(Category::getSort,"2");
//        }
      lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

//        Long id = category.getId();

            List<Category> list = categoryService.list(lqw);
            log.info("list查询数据为 = {}",list.toString());
            return R.success(list);



    }



    /**
     * 根据id来删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String>remove( Long ids){
        log.info("传入的id为 = {}",ids);

//            categoryService.removeById(ids);
        categoryService.remove(ids);




         return R.success("分类信息删除成功");
    }
    @PostMapping
    public R<String>AddSort(@RequestBody Category category){
        //添加商品分类和添加套餐分类
        log.info("添加类型 = {}",category.getType());
        if(category.getType().equals(1)){
            categoryService.save(category);
            return R.success("添加成功");

        }else if(category.getType().equals(2)){
            categoryService.save(category);
            return R.success("添加成功");
        }
        return R.error("添加失败");


    }

    /**
     *
     * @param category
     * @return
     */
    @PutMapping
    public  R<String> UpdateDate(@RequestBody Category category){

//        long id = Thread.currentThread().getId();
//        log.info("线程id为: {}",id);
//        categoryService.update(category,null);
        //只会更新不为null的数字
        categoryService.updateById(category);

        return R.success("修改成功·");
    }



}
