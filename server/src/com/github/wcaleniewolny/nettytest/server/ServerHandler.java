package com.github.wcaleniewolny.nettytest.server;

//import com.github.wcaleniewolny.nettytest.clie;
import com.github.wcaleniewolny.nettytest.common.PacketUtils;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {
    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private BlockingQueue<Object[]> packets = new LinkedBlockingQueue<Object[]>();
    private Thread packetHandleThread;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.out.println("YOM");
        if(packet.isPriority()) {
            MainServer.getMenager().callEvent(packet, channelHandlerContext.channel());
        } else {
            this.packets.add(new Object[]{packet, channelHandlerContext.channel()});
        }
    }
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("new packet!!!! -> ");
//    }
        @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        System.out.println("[SEVER] NEW CLIENT JOINDED! IP: " + incoming.remoteAddress());
        MainServer.getConnectionList().put(incoming.id().asLongText(), new ClientConnection());
        PacketUtils.sendPacket(new MsgPacket("X"), incoming);
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        MainServer.getConnectionList().remove(incoming.id().asLongText());
        System.out.println("[SEVER] CLIENT LEFT! IP: " + incoming.remoteAddress());
        if(MainServer.getSnakeGame().isRunning()){
            MainServer.getSnakeGame().surrenderSnake(ctx.channel().id().asLongText());
        }

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("NEW ERROR ->" + cause.toString());
        return;
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("channelReadComplete");
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.packetHandleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(ctx.channel().isActive()) {
                        Object[] objects = packets.take();
                        MainServer.getMenager().callEvent((Packet) objects[0], (Channel) objects[1]);
                    }
                } catch(InterruptedException e) {
                } catch(Throwable t) {
                    exceptionCaught(null, t);
                }
            }
        });
        this.packetHandleThread.setName("Event Analiser");
        this.packetHandleThread.start();
    }
}
