package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.UsersMapper;
import com.sgj.microschoolsystem.model.Users;
import com.sgj.microschoolsystem.model.bo.UsersboReg;
import com.sgj.microschoolsystem.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

@RestController
public class LoginController {

    @Autowired
    QRCodeUtils qrCodeUtils;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    FastDFSClient fastDFSClient;


    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users){
        System.out.println(users.toString());
        Users resU = usersMapper.selectByPhone(users.getPhone());

        if(resU==null){

            return IMoocJSONResult.errorMsg("用户不存在");
        }else{
            System.out.println(resU.toString());
            if(!resU.getPassword().equals(users.getPassword())){
                return IMoocJSONResult.errorMsg("密码错误");
            }else{
                return IMoocJSONResult.ok(resU);
            }
        }
    }


    @PostMapping("/checkPhone")
    public IMoocJSONResult sendCheckSMS(String phone){
        Users resU = usersMapper.selectByPhone(phone);
        if(Pattern.matches("^1(3|4|5|7|8)\\d{9}$",phone)){//检查手机号码有效性
            SMSUtils.checkPhone(phone);
        }else if(resU!=null){
            return IMoocJSONResult.errorMsg("该号码已注册");
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/foundPasswordBack")
    public IMoocJSONResult foundPasswordBack(String phone){
        Users resU = usersMapper.selectByPhone(phone);
        if(Pattern.matches("^1(3|4|5|7|8)\\d{9}$",phone)){//检查手机号码有效性
            SMSUtils.foundPassageBack(phone);
        }else if(resU==null){
            return IMoocJSONResult.errorMsg("该号码未注册");
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/updatePassage")
    public IMoocJSONResult updatePassage(String phone,String password,String checkNo){
        System.out.println(phone+" "+password+" "+checkNo);
        Users resU = usersMapper.selectByPhone(phone);
        if(resU==null){
            return IMoocJSONResult.errorMsg("该号码未注册");
        } else if(StringUtils.isBlank(password)){//密码为空
            return IMoocJSONResult.errorMsg("");
        }else if(!checkNo.equals(PhoneCodeUpdatePasswordRel.get(phone))) {
            System.out.println(phone+":"+PhoneCodeUpdatePasswordRel.get(checkNo));
            return IMoocJSONResult.errorMsg("验证码错误");
        }
        resU.setPassword(password);
        usersMapper.updateByPrimaryKey(resU);

        return IMoocJSONResult.ok(resU);
    }

    @PostMapping("/register")
    public IMoocJSONResult register(@RequestBody UsersboReg usersboReg) throws Exception {
        Users resU = usersMapper.selectByPhone(usersboReg.getPhone());
        Users u=usersMapper.selectByUsername(usersboReg.getUsername());
        if(resU!=null){
            return IMoocJSONResult.errorMsg("该号码已注册");
        }else if(u!=null){
            return IMoocJSONResult.errorMsg("用户名已经存在");
        }else if(!usersboReg.getCheckNo().equals(PhoneCodeRegisterRel.get(usersboReg.getPhone()))) {
            System.out.println(usersboReg.getPhone()+":"+PhoneCodeRegisterRel.get(usersboReg.getPhone()));
            return IMoocJSONResult.errorMsg("验证码错误");
        }else{
            Users newUser=new Users();
            newUser.setNickname(usersboReg.getNickname());
            newUser.setPhone(usersboReg.getPhone());
            newUser.setUsername(usersboReg.getUsername());
            newUser.setPassword(usersboReg.getPassword());
            newUser.setId(String.valueOf(Id.next()));
            newUser.setFaceImage("M00/00/00/rBD8W1zRWCqAXA-qAACDjM1DQf8002_100x100.png");
            newUser.setFaceImageBig("M00/00/00/rBD8W1zRWCqAXA-qAACDjM1DQf8002.png");
            newUser.setSchoolName(usersboReg.getSchoolname());
            //创建二维码
            String qrCodePath="D://pngs//"+newUser.getId()+"qrcode.png";//二维码地址
            qrCodeUtils.createQRCode(qrCodePath,"micro_school:"+newUser.getUsername());//创建二维码
            MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);//转换为multipartFile
            //上传
            String qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
            newUser.setQrcode(qrCodeUrl);
            usersMapper.insert(newUser);

            return IMoocJSONResult.ok(newUser);
        }

    }
}
