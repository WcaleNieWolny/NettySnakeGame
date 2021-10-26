package com.github.wcaleniewolny.nettytest.client.netty;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.github.wcaleniewolny.nettytest.common.enums.ClientTypeEnum;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ClientDefinePacket;
import com.sun.tools.javac.Main;
import io.netty.channel.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private BlockingQueue<Packet> packets = new LinkedBlockingQueue<Packet>();
    private Thread packetHandleThread;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        System.out.println("NEW PACKET :) CLASS: " + packet.getClass());
        if(packet.isPriority()) {
            MainClient.getMenager().callEvent(packet, channelHandlerContext.channel());
        } else {
            this.packets.add(packet);
        }

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.packetHandleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(ctx.channel().isActive()) {
                        Packet packet = packets.take();
                        MainClient.getMenager().callEvent(packet, null);
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
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("NEW ERROR ->" + cause.toString());
        return;
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        System.out.println("FUCK @@@");
        if(MainClient.getGamePanel().isRunning()){
            MainClient.getFrame().setVisible(false);
            MainClient.getFrame().dispose();
            System.exit(0);
        }

    }

}
