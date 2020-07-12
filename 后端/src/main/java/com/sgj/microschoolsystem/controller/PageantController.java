package com.sgj.microschoolsystem.controller;

import com.sgj.microschoolsystem.mapper.PageantLikeMapper;
import com.sgj.microschoolsystem.mapper.PageantMapper;
import com.sgj.microschoolsystem.model.Pageant;
import com.sgj.microschoolsystem.model.PageantLike;
import com.sgj.microschoolsystem.model.bo.PageantListAll;
import com.sgj.microschoolsystem.utils.IMoocJSONResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/pa")
public class PageantController {

    @Autowired
    private PageantMapper pageantMapper;

    @Autowired
    private PageantLikeMapper pageantLikeMapper;

    @PostMapping("/check_pageant_new")
    public IMoocJSONResult check_pageant_new(int first){
        List<PageantListAll> res = pageantMapper.selectAll();
        //List按照点赞数排序
        Collections.sort(res, (o1, o2) -> {
                Integer i1=o1.getLikeCount();
                Integer i2=o2.getLikeCount();
                if(i1>i2){
                    return 1;
                }else if(i1<i2){
                    return -1;
                }else{
                    return 0;
                }
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

    @PostMapping("/pageant_vote")
    public IMoocJSONResult pageant_vote(String voter,String poster){
        Integer voterCnt = pageantLikeMapper.countVoter(voter);
        System.out.println(voterCnt);
        if(voterCnt>=5){
            return IMoocJSONResult.errorMsg("您已经投了5票了,不能再偷了");
        }

        //点赞列表添加
        PageantLike pageantLike=new PageantLike();
        pageantLike.setCreateTime(new Date());
        pageantLike.setId(Sid.next());
        pageantLike.setLikerId(voter);
        pageantLike.setPageantId(poster);
        pageantLikeMapper.insert(pageantLike);

        //更新点赞表
        Pageant pageant = pageantMapper.selectByPrimaryKey(poster);
        pageant.setLikeCount(pageant.getLikeCount()+1);
        pageantMapper.updateByPrimaryKey(pageant);

        return IMoocJSONResult.ok();
    }

}
