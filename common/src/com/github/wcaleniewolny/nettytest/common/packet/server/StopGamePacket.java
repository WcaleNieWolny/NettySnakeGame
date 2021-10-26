package com.github.wcaleniewolny.nettytest.common.packet.server;

import com.github.wcaleniewolny.nettytest.common.enums.ClientTypeEnum;
import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
@NoArgsConstructor
public class StopGamePacket implements Packet {

    @NonNull
    public SnakeEndEnum endEnum;
    @NonNull
    public String winner;

    @Override
    public void read(NetInput in) throws IOException {
        int i = in.readVarInt();
        endEnum = Objects.requireNonNull(SnakeEndEnum.getFromI(i));
        if(endEnum == SnakeEndEnum.ONE_WINNER){
            this.winner = in.readString();
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(endEnum.i);
        if(endEnum == SnakeEndEnum.ONE_WINNER){
            out.writeString(winner);
        }
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    public enum SnakeEndEnum {
        NO_WINNERS (0),
        ONE_WINNER (1),
        YOU_LOSE(3);

        public int i;

        private SnakeEndEnum(int i) {
            this.i = i;
        }
        public static SnakeEndEnum getFromI(int i){
            if(i == 0){
                return NO_WINNERS;
            }else if(i == 1){
                return ONE_WINNER;
            }else if(i == 3){
                return YOU_LOSE;
            }
            return null;
        }
    }

}
