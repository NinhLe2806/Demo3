package vn.vnpay.sp2.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.sp2.common.Constant;
import vn.vnpay.sp2.controller.RouterController;

@Slf4j
public class NettyServer {
    public static void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 128)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(
                     new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) {
                             ChannelPipeline p = ch.pipeline();
                             p.addLast(new HttpRequestDecoder());
                             p.addLast(new HttpResponseEncoder());
                             p.addLast(new HttpObjectAggregator(64 * 1024));

                             p.addLast(RouterController.getInstance());

                         }
                     });

            ChannelFuture f = b.bind(Constant.NETTY_PORT).sync();
            log.info("Server started and listening on port {} ", Constant.NETTY_PORT);

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Error when starting Netty Server:{}", e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
