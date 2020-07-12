package com.sgj.microschoolsystem.controller;

import com.sgj.enums.MsgActionEnum;
import com.sgj.enums.OperatorFriendRequestTypeEnum;
import com.sgj.enums.SearchFriendsStatusEnum;
import com.sgj.microschoolsystem.mapper.*;
import com.sgj.microschoolsystem.model.*;
import com.sgj.microschoolsystem.model.bo.Usersbo;
import com.sgj.microschoolsystem.model.bo.UsersboMyFriends;
import com.sgj.microschoolsystem.model.bo.UsersboNickname;
import com.sgj.microschoolsystem.utils.FastDFSClient;
import com.sgj.microschoolsystem.utils.FileUtils;
import com.sgj.microschoolsystem.utils.IMoocJSONResult;
import com.sgj.microschoolsystem.utils.JsonUtils;
import com.sgj.netty.DataContent;
import com.sgj.netty.UserChannelRel;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private ManagerTableMapper managerTableMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private FriendRequestAndUserMapper friendRequestAndUserMapper;

    @PostMapping("/checkerManager")
    public IMoocJSONResult checkerManager(String id){
        if(StringUtils.isBlank(id)){
            return IMoocJSONResult.errorMsg("");
        }

        ManagerTable key = managerTableMapper.selectByPrimaryKey(id);

        if(key==null){
            return IMoocJSONResult.errorMsg("您不是本校管理员！");
        }else{
            return IMoocJSONResult.ok();
        }
    }

    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody Usersbo usersbo) throws Exception {
        System.out.println(usersbo);
        //获取前端传过来的base64字符串，然后转换为文件对象在上传
        String base64Data=usersbo.getImageData();
        System.out.println(base64Data);
        String userFacePath="D:\\pngs\\"+usersbo.getId()+"userface64.png";
        FileUtils.base64ToFile(userFacePath,base64Data);
        //上传文件到fastDFS
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url=fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);
        //获取缩略图的url
        String thump="_100x100.";
        String arr[]=url.split("\\.");
        String thumpImgUrl=arr[0]+thump+arr[1];

        //更新用户头像
        Users users=usersMapper.selectByPrimaryKey(usersbo.getId());
        users.setFaceImage(thumpImgUrl);
        users.setFaceImageBig(url);
        usersMapper.updateByPrimaryKey(users);

        return IMoocJSONResult.ok(users);
    }

    @PostMapping("/setNickname")
    public IMoocJSONResult setNickname(@RequestBody UsersboNickname usersboNickname) throws Exception {

        //更新用户昵称
        Users users=usersMapper.selectByPrimaryKey(usersboNickname.getId());
        users.setNickname(usersboNickname.getNickname());
        usersMapper.updateByPrimaryKey(users);

        return IMoocJSONResult.ok(users);
    }

    @PostMapping("/searchUser")
    public IMoocJSONResult searchUser(String myUserId,String friendUsername) throws Exception {

        //0.判断myUserId friendUsername不能为空
        if(StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return IMoocJSONResult.errorMsg("");
        }

        Users me = usersMapper.selectByPrimaryKey(myUserId);
        //搜索好友根据账号做匹配查询

        //前置条件 -1.搜索的用户如果不存在，返回[无此用户]
        Users users = usersMapper.selectByUsername(friendUsername);
        if(users==null){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.USER_NOT_EXIST.msg);
        }
        //前置条件 -2.搜索的账号是你自己，返回[不能添加自己]
        if(me.getUsername().equals(friendUsername)){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.NOT_YOURSELF.msg);
        }
        //前置条件 -3.搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        List<String> friends = myFriendsMapper.selectByMyUserId(myUserId);
        if(friends.contains(friendUsername)){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.ALREADY_FRIENDS.msg);
        }

        return IMoocJSONResult.ok(users);
    }

    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId,String friendUsername) throws Exception {

        //没写service层，代码冗余比较多

        //0.判断myUserId friendUsername不能为空
        if(StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return IMoocJSONResult.errorMsg("");
        }

        Users me = usersMapper.selectByPrimaryKey(myUserId);
        //搜索好友根据账号做匹配查询

        //前置条件 -1.搜索的用户如果不存在，返回[无此用户]
        Users friend = usersMapper.selectByUsername(friendUsername);
        if(friend==null){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.USER_NOT_EXIST.msg);
        }
        //前置条件 -2.搜索的账号是你自己，返回[不能添加自己]
        if(me.getUsername().equals(friendUsername)){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.NOT_YOURSELF.msg);
        }
        //前置条件 -3.搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        List<String> friends = myFriendsMapper.selectByMyUserId(myUserId);
        if(friends.contains(friendUsername)){
            return IMoocJSONResult.errorMsg(SearchFriendsStatusEnum.ALREADY_FRIENDS.msg);
        }


        FriendsRequest request=new FriendsRequest();
        //查询
        FriendsRequest friendsRequest=friendsRequestMapper.selectBySendUserIdAndAcceptUSerId(myUserId,friend.getId());
        if(friendsRequest==null){//如果朋友请求表里面没有相应的记录并且
            //添加friendsRequest记录

            request.setId(sid.nextShort());
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());

            friendsRequestMapper.insert(request);
            return IMoocJSONResult.ok(request);
        }else{//该用户已经是你的好友
            return IMoocJSONResult.errorMsg("你已经添加过该好友...");
        }

    }

    //查找添加好友请求
    @PostMapping("/queryFriendRequests")
    public IMoocJSONResult queryFriendRequests(String userId) throws Exception {
        //0.判断myUserId不能为空
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("");
        }
        List<FriendRequestInfo> infos = friendRequestAndUserMapper.queryFriendRequestList(userId);

        //查询用户接受到的朋友申请
        return IMoocJSONResult.ok(infos);
    }

    @PostMapping("/operFriendRequest")
    public IMoocJSONResult operFriendRequest(String acceptUserId,String sendUserId,Integer operType) throws Exception {

        //0.判断acceptUserId sendUserId operType不能为空
        if(StringUtils.isBlank(acceptUserId)||StringUtils.isBlank(sendUserId) || operType==null){
            return IMoocJSONResult.errorMsg("");
        }
       //判断枚举代码是否错误
       if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
           return IMoocJSONResult.errorMsg("");
       }

       if(operType==OperatorFriendRequestTypeEnum.IGNORE.type){
           //判断如果忽略好友请求，直接删除好友请求的数据库表记录
            friendsRequestMapper.deleteSendUserIdAndAcceptUserId(sendUserId,acceptUserId);
       }else if(operType==OperatorFriendRequestTypeEnum.PASS.type){
           //判断如果通过好友请求，互相增加好友记录到数据库对应的表,然后删除好友请求的数据库表记录
           MyFriends m1=new MyFriends();
           MyFriends m2=new MyFriends();
           m1.setId(Sid.next());
           m1.setMyUserId(sendUserId);
           m1.setMyFriendUserId(acceptUserId);
           m2.setId(Sid.next());
           m2.setMyUserId(acceptUserId);
           m2.setMyFriendUserId(sendUserId);
           myFriendsMapper.insert(m1);
           myFriendsMapper.insert(m2);
           friendsRequestMapper.deleteSendUserIdAndAcceptUserId(sendUserId,acceptUserId);



           Channel sendChannel = UserChannelRel.get(sendUserId);
           if(sendChannel!=null){
               //使用websocket主动推送消息到请求发起者，更新他们的通讯录列表为最新
               DataContent dataContent=new DataContent();
               dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

               sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));//利用websocket传递信息
           }
       }


        List<UsersboMyFriends> myFriends = friendRequestAndUserMapper.queryMyFriends(acceptUserId);

        return IMoocJSONResult.ok(myFriends);
    }

    //查询我的好友列表
    @PostMapping("/myFriends")
    public IMoocJSONResult myFriends(String userId) throws Exception {

        //0.判断userId不能为空
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("");
        }

        //1.数据库查询好友列表
        List<UsersboMyFriends> myFriends = friendRequestAndUserMapper.queryMyFriends(userId);

        return IMoocJSONResult.ok(myFriends);
    }

    /**
     * 用户手机端获取未签收的消息列表
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/getUnreadMsgList")
    public IMoocJSONResult getUnreadMsgList(String acceptUserId) throws Exception {
        //0.判断acceptUserId不能为空
        if(StringUtils.isBlank(acceptUserId)){
            return IMoocJSONResult.errorMsg("");
        }

        //查询未读消息列表
        List<ChatMsg> unreadMsgList = chatMsgMapper.getUnreadMsgList(acceptUserId);

        //返回未读消息列表
        return IMoocJSONResult.ok(unreadMsgList);
    }

}
