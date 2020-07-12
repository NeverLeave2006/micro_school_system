package com.sgj.microschoolsystem.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import java.util.Random;

public class SMSUtils {

    public static void checkPhone(String phone){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIAZeEujqKlRZT", "cLn3w7oBnMgXKwL9rSJlEbXo1h3wgH");
        IAcsClient client = new DefaultAcsClient(profile);

        Random r=new Random();
        String code=Integer.toString(r.nextInt(10000));
        PhoneCodeRegisterRel.put(phone,code);
        System.out.println(phone+":"+code);
        System.out.println(phone+":"+PhoneCodeRegisterRel.get(phone));

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "微校系统");
        request.putQueryParameter("TemplateCode", "SMS_165117714");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static void foundPassageBack(String phone){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIAZeEujqKlRZT", "cLn3w7oBnMgXKwL9rSJlEbXo1h3wgH");
        IAcsClient client = new DefaultAcsClient(profile);

        Random r=new Random();
        String code=Integer.toString(r.nextInt(10000));
        PhoneCodeUpdatePasswordRel
                .put(phone,code);
        System.out.println(phone+":"+code);
        System.out.println(phone+":"+PhoneCodeUpdatePasswordRel.get(phone));

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "微校系统");
        request.putQueryParameter("TemplateCode", "SMS_165107826");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
