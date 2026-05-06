package org.example.editions;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.example.Server;

import java.util.Arrays;

public class PlayedRenderer {
    private int spawnRandom = (int)(Math.random() * 2365);
    private int cameraX = spawnRandom, cameraY = spawnRandom, speed = 12;
    private static int[][] grid = new int[32][32];
    private final int playerZ = 64, length = 20;
    static {
        // Created map
        for (int[] ints : grid) {
            Arrays.fill(ints, 1);
        }
    }
    public void _00000021230032130_(ChannelHandlerContext ctx) {
        int w = grid.length;
        int h = grid[0].length;
        int payloadSize = 12 + (w * h * 4);
        ByteBuf buffer = ctx.alloc().buffer(payloadSize + 4);
        buffer.writeInt(payloadSize);
        buffer.writeInt(12);
        buffer.writeInt(w);
        buffer.writeInt(h);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                buffer.writeInt(grid[i][j]);
            }
        }
        ctx.writeAndFlush(buffer);
    }
    public void spawnPlayer(ChannelHandlerContext ctx) {
        ByteBuf buffer = ctx.alloc().buffer(length + 4);
        buffer.writeInt(length);
        buffer.writeInt(15);
        buffer.writeInt(playerZ);
        buffer.writeInt(cameraX);
        buffer.writeInt(cameraY);
        buffer.writeInt(ctx.channel().hashCode());
        ctx.writeAndFlush(buffer);
    }
    public void cameraMap(ChannelHandlerContext ctx, ByteBuf buf) {
        while (buf.isReadable()) {
            byte keys = buf.readByte();
            switch (keys) {
                case 2 -> {
                    cameraY -= speed;
                    if (cameraY <= -2365) cameraY = -2365;
                    cameraY(ctx, cameraY);
                }
                case 5 -> {
                    cameraY += speed;
                    if (cameraY >= 2365) cameraY = 2365;
                    cameraY(ctx, cameraY);
                }
                case 7 -> {
                    cameraX -= speed;
                    if (cameraX <= -2365) cameraX = -2365;
                    cameraX(ctx, cameraX);
                }
                case 9 -> {
                    cameraX += speed;
                    if (cameraX >= 2365) cameraX = 2365;
                    cameraX(ctx, cameraX);
                }
            }
        }
    }
    public void cameraX(ChannelHandlerContext ctx, int cameraX) {
        int length = 12;
        ByteBuf out_buf = ctx.alloc().buffer(length + 4);
        out_buf.writeInt(length);
        out_buf.writeInt(8); // ID - X
        out_buf.writeInt(ctx.channel().hashCode());
        out_buf.writeInt(cameraX);
        Server.group.writeAndFlush(out_buf);
    }
    public void cameraY(ChannelHandlerContext ctx, int cameraY) {
        int length = 12;
        ByteBuf out_buf = ctx.alloc().buffer(length + 4);
        out_buf.writeInt(length);
        out_buf.writeInt(9); // ID - Y
        out_buf.writeInt(ctx.channel().hashCode());
        out_buf.writeInt(cameraY);
        Server.group.writeAndFlush(out_buf);
    }
}