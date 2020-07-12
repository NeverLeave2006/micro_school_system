package com.sgj.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * 
 * @Description: 用于监测channel的心跳handler
 * 继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
 *
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		//判断evt是否是IdleStateEvent(用于触发用户事件，包含 读空闲/写空闲/读写空闲)
		if(evt instanceof IdleStateEvent){
			IdleStateEvent event=(IdleStateEvent)evt; //强制类型转换
			if(event.state()== IdleState.READER_IDLE){
				System.out.println("进入读空闲...");
			}else if(event.state()== IdleState.WRITER_IDLE){
				System.out.println("进入写空闲...");
			}else if(event.state()== IdleState.ALL_IDLE){
				System.out.println("channel 关闭前，user的数量为："+ ChatHandler.users.size());
				//关闭无用channel，防止资源浪费
				Channel channel = ctx.channel();
				channel.close();
			}
		}

	}
}
