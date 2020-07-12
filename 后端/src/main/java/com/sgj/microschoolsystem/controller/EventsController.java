package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.EventsMapper;
import com.sgj.microschoolsystem.model.Events;
import com.sgj.microschoolsystem.model.bo.EventsRequestBean;
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
@RequestMapping("/e")
public class EventsController {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private EventsMapper eventsMapper;

    @PostMapping("/post_event")
    public IMoocJSONResult post_event(@RequestBody EventsRequestBean eventsRequestBean) {

        System.out.println(eventsRequestBean);
        //0.判断msg Data不能为空
        if(StringUtils.isBlank(eventsRequestBean.getName())
                ||StringUtils.isBlank(eventsRequestBean.getContent())
                ||StringUtils.isBlank(eventsRequestBean.getImgData())
                ||StringUtils.isBlank(eventsRequestBean.getPosterid())
                ||StringUtils.isBlank(eventsRequestBean.getSchoolName())
                ||StringUtils.isBlank(eventsRequestBean.getDept())
                ||StringUtils.isBlank(eventsRequestBean.getUrl())){
            return IMoocJSONResult.errorMsg("信息不完整");
        }

        //获取前端传过来的base64字符串，然后转换为文件对象在上传
        String base64Data=eventsRequestBean.getImgData();
        System.out.println(base64Data);
        String userFacePath="D:\\pngs\\event_"+eventsRequestBean.getPosterid()+"userface64.png";
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

        Events events=new Events();

        events.setId(Sid.next());
        events.setName(eventsRequestBean.getName());
        events.setContent(eventsRequestBean.getContent());
        events.setImg(url);
        events.setPosterid(eventsRequestBean.getPosterid());
        events.setSchoolName(eventsRequestBean.getSchoolName());
        events.setCreateTime(new Date());
        events.setUrl(eventsRequestBean.getUrl());
        events.setDept(eventsRequestBean.getDept());

        eventsMapper.insert(events);

        return IMoocJSONResult.ok();
    }

    @PostMapping("/check_event_new")
    public IMoocJSONResult check_event_new(String school,int first){
        List<Events> res = eventsMapper.selectMySchool(school);
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
