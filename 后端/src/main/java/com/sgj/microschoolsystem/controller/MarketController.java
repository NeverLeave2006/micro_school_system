package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.MarketMapper;
import com.sgj.microschoolsystem.model.Market;
import com.sgj.microschoolsystem.model.bo.MarketItem;
import com.sgj.microschoolsystem.model.bo.MarketRequestBean;
import com.sgj.microschoolsystem.utils.FastDFSClient;
import com.sgj.microschoolsystem.utils.FileUtils;
import com.sgj.microschoolsystem.utils.IMoocJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/m")
public class MarketController {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private MarketMapper marketMapper;

    @PostMapping("/post_market")
    public IMoocJSONResult post_market(@RequestBody MarketRequestBean marketRequestBean) {

        System.out.println(marketRequestBean);
        //0.判断msg Data不能为空
        if(StringUtils.isBlank(marketRequestBean.getName())
                ||StringUtils.isBlank(marketRequestBean.getPosterId())
                ||StringUtils.isBlank(marketRequestBean.getDescription())
                ||StringUtils.isBlank(marketRequestBean.getConnect())
                ||StringUtils.isBlank(marketRequestBean.getPrice())
                ||StringUtils.isBlank(marketRequestBean.getImgData())
                ||StringUtils.isBlank(marketRequestBean.getSchoolname())){
            return IMoocJSONResult.errorMsg("信息不完整");
        }

        System.out.println(marketRequestBean);

        //获取前端传过来的base64字符串，然后转换为文件对象在上传
        String base64Data=marketRequestBean.getImgData();
        System.out.println(base64Data);
        String userFacePath="D:\\pngs\\"+marketRequestBean.getPosterId()+"userface64.png";
        String url="";
        try {
            FileUtils.base64ToFile(userFacePath,base64Data);
            //上传文件到fastDFS
            MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
            url=fastDFSClient.uploadBase64(faceFile);
            System.out.println(url);
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("图片上传失败");
        }



        Market market=new Market();
        market.setId(Sid.next());
        market.setCreateTime(new Date());
        market.setName(marketRequestBean.getName());
        market.setDescription(marketRequestBean.getDescription());
        market.setImg(url);
        market.setPrice(marketRequestBean.getPrice());
        market.setConnect(marketRequestBean.getConnect());
        market.setPosterId(marketRequestBean.getPosterId());
        market.setSchool(marketRequestBean.getSchoolname());

        marketMapper.insert(market);

        return IMoocJSONResult.ok(market);
    }

    @PostMapping("/check_market_new")
    public IMoocJSONResult check_market_new(String school,int first){
        if(StringUtils.isBlank(school)){
            return IMoocJSONResult.errorMsg("参数为空");
        }
        List<MarketItem> res=marketMapper.selectMySchool(school);
        //List按照时间排序
        Collections.sort(res, (o1, o2) -> {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d1=format.parse(format.format(o1.getCreateTime()));
                Date d2=format.parse(format.format(o2.getCreateTime()));
                if(d1.getTime()<d2.getTime()){
                    return 1;
                }else if(d1.getTime()>d2.getTime()){
                    return -1;
                }else{
                    return 0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        if(first<0){
            return IMoocJSONResult.errorMsg("错误参数");
        }

        //分页
        if(first>=res.size()){
            return IMoocJSONResult.errorMsg("没有了");
        }else if(first+10>res.size()){
            return IMoocJSONResult.ok(res.subList(first,res.size()));
        }else{
            return IMoocJSONResult.ok(res.subList(first,first+10));
        }

    }



}
