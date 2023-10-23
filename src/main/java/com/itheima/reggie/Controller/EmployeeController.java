package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.EmployeeService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;

import com.itheima.reggie.entry.Employee;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

       // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
//2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

//
//        String password = employee.getPassword();
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//
//        //2、根据页面提交的用户名username查询数据库
//        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername,employee.getUsername());
//        Employee emp = employeeService.getOne(queryWrapper); //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        log.info("用户id为：{}",emp.getId());
        return R.success(emp);
//

//    }

        /**
         * 员工退出
         * @param request
         * @return
         */
//    @PostMapping("/logout")
//    public R<String> logout(HttpServletRequest request){
//        //清理Session中保存的当前登录员工的id
//        request.getSession().removeAttribute("employee");
//        return R.success("退出成功");
//    }

    }

    @PostMapping("/logout")
    public R <String>Logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");


        return R.success("退出成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //分页器 初始页面page和页面大小pageSize
       Page pageInfo = new Page(page,pageSize);
       LambdaQueryWrapper<Employee>lqw = new LambdaQueryWrapper<Employee>();

        //过滤查询
        lqw.like(StringUtils.isNotEmpty(name),Employee::getUpdateTime,name);



        //排序查询
        lqw.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,lqw);

        //保存


        return  R.success(pageInfo);

    }

    /**
     * 禁用账户
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
            log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为: {}",id);

        //Long epid= (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(epid);
        employeeService.updateById(employee);


        return R.success("员工信息修改成功");
    }




    @PostMapping
    public R<String>AddStaff(HttpServletRequest request,@RequestBody Employee employee){
      log.info("新增员工，员工信息：{}",employee.toString());
        LambdaQueryWrapper<Employee>queryWrapper = new LambdaQueryWrapper<>();
//        设置初始密码，进行MD5加密
       employee.setPassword( DigestUtils.md5DigestAsHex("123456".getBytes()));


       //获取当前用户的id
     // Long id=(Long) request.getSession().getAttribute("employee");

     // employee.setCreateTime(LocalDateTime.now());
      //employee.setUpdateTime(LocalDateTime.now());

        //employee.setCreateUser(id);
        //employee.setUpdateUser(id);
       employeeService.save(employee);



        return R.success("新增员工成功");

    }

    /**
     * 判断数据库中该数据是否存在
     * @param id 前端传入id值
     * @return 返回json数据
     */
    @GetMapping("/{id}")
    public R<Employee>GetByid(@PathVariable String id){

        Employee byId = employeeService.getById(id);
        if(byId!=null){
            return R.success(byId);
        }
        return R.error("未查询到用户信息");
    }
}
