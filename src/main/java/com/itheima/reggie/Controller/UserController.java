package com.itheima.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.Service.UserService;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entry.User;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping ("/login")
    public R<User>login(@RequestBody Map<String, String> mp, HttpSession session){
        String phone =mp.get("phone");
        String code = mp.get("code");
        Object codeShession = session.getAttribute(phone);
        log.info("session的数值为 = {}",codeShession.toString());
        log.info("phone的值的数值为 = {}",phone);

        if(codeShession!=null && codeShession.equals(code)){
            LambdaQueryWrapper<User>lqw= new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User us = userService.getOne(lqw);

//            log.info("us的数值为 = {}",us.toString());
//            log.info("数据库返回的数据为 = {}",one.toString());

            if(us==null){
                us= new User();
               us.setPhone(phone);
               us.setStatus(1);
               userService.save(us);


            }
            session.setAttribute("user",us.getId());

            return R.success(us);

        }
        return R.error("失败");



    }



    @PostMapping("/sendMsg")
    public R<String>getCode(@RequestBody User us, HttpSession session){
        log.info("us的值为 = {}",us.toString());
        String phone = us.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
           String code = ValidateCodeUtils.generateValidateCode(6).toString();
//           String code="111111";
            log.info("验证码code的值为 = {}",code);
           session.setAttribute(phone,code);
            return R.success("短信发送成功");

       }
        return R.error("短信发送失败");


    }

    @PostMapping("/loginout")
    public R<String>Loginout(User user,HttpSession session){
        Long cruuentId = BaseContext.getCruuentId();
        user.setId(cruuentId);
        LambdaQueryWrapper<User>lqwuser= new LambdaQueryWrapper<>();
        lqwuser.eq(cruuentId!=null,User::getId,user.getId());
        User one = userService.getOne(lqwuser);
        session.removeAttribute(one.getPhone());
        return R.success("退出成功");
    }
}
