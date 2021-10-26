package com.github.wcaleniewolny.nettytest.client.netty;

import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    private int port;
    private String host;
    private boolean isRunning = true;
    private Channel channel;
    private EventLoopGroup group;

    public NettyClient(int port, String host) {
        this.port = port;
        this.host = host;
    }
    public void run() {
        this.group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatInitializer());
            this.channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sendPacket(Packet packet){
        if(!this.channel.isActive()){
            return;
        }
        this.channel.writeAndFlush(packet).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()) {
                System.out.println("SUKCESS PACKET SEND! PACKET: " + packet.getClass().getName());
            } else {
                System.out.println("FUCK!");
            }
        });
    }
    public void shutdown(){
        this.group.shutdownGracefully();
    }

    public Channel getChannel() {
        return channel;
    }
}
