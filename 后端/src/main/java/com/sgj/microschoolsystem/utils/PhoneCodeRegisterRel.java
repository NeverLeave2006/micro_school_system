package com.sgj.microschoolsystem.utils;


import java.util.HashMap;

/**
 *  @Description：用户id和channel的关联关系
 */
public class PhoneCodeRegisterRel {
    private static HashMap<String, String> manager=new HashMap<>();

    public static void put(String phone,String code){
        manager.put(phone,code);
    }

    public static String get(String phone){
        return manager.get(phone);
    }

    public static void output(){
        for(HashMap.Entry<String, String> entry : manager.entrySet()){
            System.out.println("Phone:"+entry.getKey()+",Code:"+entry.getValue());
        }
    }
}
