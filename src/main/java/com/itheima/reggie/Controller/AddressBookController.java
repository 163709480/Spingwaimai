package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.Service.AddressBookService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entry.AddressBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> page(AddressBook addressBook){
        Long cruuentId = BaseContext.getCruuentId();
        addressBook.setUserId(cruuentId);
        log.info("页面id数值为 = {}",cruuentId);

        LambdaQueryWrapper<AddressBook> lqw= new LambdaQueryWrapper<>();
        lqw.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        List<AddressBook> list = addressBookService.list(lqw);


        return R.success(list);

    }

    /**
     * 添加地址
     * @param addressBook
     * @cruuentId
     * @return
     */
    @PostMapping
    public R<String>Address(@RequestBody AddressBook addressBook, HttpSession session){
        if(addressBook!=null){
//            session.getId().var
            Long cruuentId = BaseContext.getCruuentId();
            log.info("id值打印 = {}",cruuentId);
            addressBook.setUserId(cruuentId);
            addressBookService.save(addressBook);

            return R.success("添加成功");
        }
        return R.success("添加失败");


    }

    /**
     * 地址编辑返回数值
     * @param addid
     * @return
     */
    @GetMapping("/{addid}")
    public R<AddressBook> disAddress(@PathVariable String addid){
        BaseContext.getCruuentId();
        log.info("addid的值为 = {}",addid);
    LambdaQueryWrapper<AddressBook>lqw =new LambdaQueryWrapper<>();
    lqw.eq(AddressBook::getId,addid);
        AddressBook one = addressBookService.getOne(lqw);


        return R.success(one);
    }

    /**
     * 修改地址信息
     * @return
     */
    @PutMapping
    public R<String>updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);


        return R.success("修改成功");
    }
    @DeleteMapping
    public R<String>DropAdd(String ids){
        LambdaQueryWrapper<AddressBook> add= new LambdaQueryWrapper<>();

        add.eq(ids!=null,AddressBook::getId,ids);
        addressBookService.remove(add);

        return R.success("删除成功");
    }
    @GetMapping("/default")
    public R<AddressBook >defaultt(AddressBook addressBook){
        Long cruuentId = BaseContext.getCruuentId();

        addressBook.setUserId(cruuentId);
        addressBook.setIsDefault(1);
        log.info("订单信息地址 = {}",addressBook);
        LambdaQueryWrapper<AddressBook>defaultadd= new LambdaQueryWrapper<>();
        defaultadd.eq(addressBook!=null,AddressBook::getUserId,addressBook.getUserId()).eq(AddressBook::getIsDefault,addressBook.getIsDefault());
        AddressBook one = addressBookService.getOne(defaultadd);
        return R.success(one);

    }
    @PutMapping("/default")
    public R<String>updateDefautl(@RequestBody AddressBook addressBook){

        Long cruuentId = BaseContext.getCruuentId();
//

        //条件更新语句
        LambdaUpdateWrapper<AddressBook>or =new LambdaUpdateWrapper<>();
        or.eq(cruuentId!=null,AddressBook::getUserId,cruuentId);
        or.set(AddressBook::getIsDefault,0);
        addressBookService.update(or);
//


        //再添加
        LambdaQueryWrapper<AddressBook>lqwadd= new LambdaQueryWrapper<>();

        lqwadd.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("更改成功");

    }





}
