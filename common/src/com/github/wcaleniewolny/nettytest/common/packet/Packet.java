package com.github.wcaleniewolny.nettytest.common.packet;

import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;

import java.io.IOException;

/**
 * A network com.github.wcaleniewolny.NettyTest.packet.
 */
public interface Packet {
    /**
     * Reads the com.github.wcaleniewolny.NettyTest.packet from the given input buffer.
     *
     * @param in The input source to read from.
     * @throws IOException If an I/O error occurs.
     */
    public void read(NetInput in) throws IOException;

    /**
     * Writes the com.github.wcaleniewolny.NettyTest.packet to the given output buffer.
     *
     * @param out The output destination to write to.
     * @throws IOException If an I/O error occurs.
     */
    public void write(NetOutput out) throws IOException;

    /**
     * Gets whether the com.github.wcaleniewolny.NettyTest.packet has handling priority.
     * If the result is true, the com.github.wcaleniewolny.NettyTest.packet will be handled immediately after being
     * decoded.
     *
     * @return Whether the com.github.wcaleniewolny.NettyTest.packet has priority.
     */
    public boolean isPriority();
}
