package com.github.wcaleniewolny.nettytest.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.UUID;

public class NettyServer {
    private int port;
    private static String salt;

    public NettyServer(int port) {
        this.port = port;
    }
    public void run(){
        Thread thread = new Thread(() -> {
            EventLoopGroup boosGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try{
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerInitializer());
                bootstrap.bind(port).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                boosGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        thread.setName("server");
        thread.start();
        MainServer.getLogger().info("Server started!");
        salt = UUID.randomUUID().toString();
    }

    public static String getSalt() {
        return salt;
    }
}
