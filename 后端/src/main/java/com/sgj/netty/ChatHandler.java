package com.sgj.netty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sgj.enums.MsgActionEnum;
import com.sgj.enums.MsgSignFlagEnum;
import com.sgj.microschoolsystem.SpringUtil;
import com.sgj.microschoolsystem.mapper.ChatMsgMapper;
import com.sgj.microschoolsystem.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	public static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) 
			throws Exception {
		// 获取客户端传输过来的消息
		String content = msg.text();
		System.out.println(content);
		Channel currentChannel=ctx.channel();

		//1.获取从客户端传来的消息
		DataContent dataContent= JsonUtils.jsonToPojo(content,DataContent.class);
		Integer action=dataContent.getAction();
		//2.判断消息类型，根据不同的类型来处理不同的业务
		//2.1当websocket第一次open的时候，初始化channel，把用户的channel和userid关联起来
		if(action== MsgActionEnum.CONNECT.type){
			//2.1当websocket第一次open的时候，初始化channel，把用户的channel和userid关联起来
			String senderId = dataContent.getChatMsg().getSenderId();
			UserChannelRel.put(senderId,currentChannel);

			//测试
			for(Channel c:users){
				System.out.println("websocket连接：");
				System.out.println(c.id().asLongText());//打印channel的长id
			}

		}else if(action== MsgActionEnum.CHAT.type){
			//2.2聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
			ChatMsg chatMsg = dataContent.getChatMsg();
			String msgText = chatMsg.getMsg();
			String receiverId = chatMsg.getReceiverId();
			String senderId = chatMsg.getSenderId();

			//保存消息到数据库，并且标记为未签收
			ChatMsgMapper chatMsgMapper= (ChatMsgMapper) SpringUtil.getBean("chatMsgMapper");
			com.sgj.microschoolsystem.model.ChatMsg msgDB=new com.sgj.microschoolsystem.model.ChatMsg();
			String msgId= Sid.next();
			msgDB.setId(msgId);
			chatMsg.setMsgId(msgId);//设置消息id,发送回去
			msgDB.setAcceptUserId(receiverId);
			msgDB.setSendUserId(senderId);
			msgDB.setCreateTime(new Date());
			msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
			msgDB.setMsg(msgText);
			chatMsgMapper.insert(msgDB);

			DataContent dataContentMsg=new DataContent();
			dataContentMsg.setChatMsg(chatMsg);
			System.out.println(chatMsg.toString());

			//发送消息
			//从全局channel中获取接受方的channel
			Channel receiverChannel=UserChannelRel.get(receiverId);
			if(receiverChannel==null){
				//TODO channel为空代表用户离线，推送消息(JPush,个推，小米推送)
			}else {
				//当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
				Channel findchannel=users.find(receiverChannel.id());
				if(findchannel!=null){
					//用户在线
					receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
				}else{
					//用户离线 TODO 推送消息

				}
			}


		}else if(action== MsgActionEnum.SIGNED.type){
			//2.3签收消息类型，针对具体的消息进行签收，修改数据库中对应信息的签收状态[已签收]

			//扩展字段在signed类型的消息中，代表需要去签收的消息id, 逗号间隔
			String msgIdsStr=dataContent.getExtand();
			System.out.println(msgIdsStr);
			String msgIds[]=msgIdsStr.split(",");

			List<String> msgIdList=new ArrayList<>();
			for(String mid:msgIds){//循环，去空
				if(StringUtils.isNotBlank(mid)){
					msgIdList.add(mid);
				}
			}
			System.out.println(msgIdList.toString());
			ChatMsgMapper chatMsgMapper= (ChatMsgMapper) SpringUtil.getBean("chatMsgMapper");
			if(msgIdList!=null&&!msgIdList.isEmpty()&&msgIdList.size()>0){
				//批量签收
				for(String mid:msgIdList){//批量更新，这样反复读写数据库，不好
					com.sgj.microschoolsystem.model.ChatMsg chatMsg = chatMsgMapper.selectByPrimaryKey(mid);
					chatMsg.setSignFlag(MsgSignFlagEnum.signed.type);
					chatMsgMapper.updateByPrimaryKey(chatMsg);
				}

			}

		}else if(action== MsgActionEnum.KEEPALIVE.type){
			System.out.println("心跳消息");

		}



	}

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//		users.remove(ctx.channel());
		System.out.println("客户端断开，channle对应的长id为：" 
							+ ctx.channel().id().asLongText());
		System.out.println("客户端断开，channle对应的短id为：" 
							+ ctx.channel().id().asShortText());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//发生异常后关闭连接（关闭channel）,随后从ChannelGroup中删除
		cause.printStackTrace();
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
