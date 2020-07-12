package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.PageantMapper;
import com.sgj.microschoolsystem.model.Pageant;
import com.sgj.microschoolsystem.model.bo.PageantboPost;
import com.sgj.microschoolsystem.utils.FastDFSClient;
import com.sgj.microschoolsystem.utils.FileUtils;
import com.sgj.microschoolsystem.utils.IMoocJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/a")
public class ActivitiesController {

    @Autowired
    private PageantMapper pageantMapper;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/post_selfile")
    public IMoocJSONResult login(@RequestBody PageantboPost pageantboPost) throws Exception {
        System.out.println(pageantboPost);
        Pageant pageant=new Pageant();
        pageant.setPoster(pageantboPost.getPoster());
        pageant.setLikeCount(0);
        pageant.setSchoolName(pageantboPost.getSchoolName());

        String base64Data=pageantboPost.getImg();
        System.out.println(base64Data);
        String userFacePath="D:\\pngs\\"+pageantboPost.getPoster()+"selfile.png";
        FileUtils.base64ToFile(userFacePath,base64Data);
        //上传文件到fastDFS
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url=fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        pageant.setImg(url);
        pageantMapper.insert(pageant);

        return IMoocJSONResult.ok(pageant);
    }
}
