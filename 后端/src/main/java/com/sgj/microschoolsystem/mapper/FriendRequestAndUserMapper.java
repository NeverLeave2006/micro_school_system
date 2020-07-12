package com.sgj.microschoolsystem.mapper;

import com.sgj.microschoolsystem.model.FriendRequestInfo;
import com.sgj.microschoolsystem.model.bo.UsersboMyFriends;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendRequestAndUserMapper {//这个是自定义的mapper，用于多表查询

    List<FriendRequestInfo> queryFriendRequestList(@Param("sendId") String sendId);

    List<UsersboMyFriends> queryMyFriends(@Param("userId") String userId);
}
