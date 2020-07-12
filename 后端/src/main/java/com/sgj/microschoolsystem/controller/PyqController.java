package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.PyqMapper;
import com.sgj.microschoolsystem.model.Pyq;
import com.sgj.microschoolsystem.model.bo.PyqRequest;
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

import java.util.Date;

@RestController
@RequestMapping("/p")
public class PyqController {

    @Autowired
    private PyqMapper pyqMapper;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/post_pyq")
    public IMoocJSONResult post_pyq(@RequestBody PyqRequest pyqRequest) throws Exception {

        //0.判断msg Data不能为空
        if(StringUtils.isBlank(pyqRequest.getMsg())||StringUtils.isBlank(pyqRequest.getImgData())||StringUtils.isBlank(pyqRequest.getPoster())){
            return IMoocJSONResult.errorMsg("");
        }

        System.out.println(pyqRequest);

        //获取前端传过来的base64字符串，然后转换为文件对象在上传
        String base64Data=pyqRequest.getImgData();
        System.out.println(base64Data);
        String userFacePath="D:\\pngs\\"+pyqRequest.getPoster()+"userface64.png";
        FileUtils.base64ToFile(userFacePath,base64Data);
        //上传文件到fastDFS
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url=fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);
        //获取缩略图的url
        String thump="_100x100.";
        String arr[]=url.split("\\.");
        String thumpImgUrl=arr[0]+thump+arr[1];

        Pyq pyq=new Pyq();
        pyq.setId(Sid.next());
        pyq.setMsg(pyqRequest.getMsg());
        pyq.setLikeCount(0);
        pyq.setPicture(url);
        pyq.setPictureSmall(thumpImgUrl);
        pyq.setPosterId(pyqRequest.getPoster());
        pyq.setCreateTime(new Date());
        pyqMapper.insert(pyq);

        return IMoocJSONResult.ok(pyq);
    }

}
