package vn.vnpay.demo3;

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
import vn.vnpay.demo3.common.Constant;
import vn.vnpay.demo3.controller.RouterController;

@Slf4j
public class NettyServer {
    public static void start() {
        // Lớp chính để khởi tạo Netty, cung cấp các phương thức để cấu hình các tham số
        // và các thành phần khác của máy chủ trước khi nó được khởi động.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // xử lý kết nối đến
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // xử lý yêu cầu và truyền tải dữ liệu

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 128) // số lượng tối đa kết nối đc chấp nhận vào hàng đợi
             .handler(new LoggingHandler(LogLevel.INFO)) // ghi lại log của kết nối
             .childHandler(
                     new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) {
                             ChannelPipeline p = ch.pipeline();
                             p.addLast(new HttpRequestDecoder());
                             p.addLast(new HttpResponseEncoder());
                             p.addLast(new HttpObjectAggregator(64 * 1024)); // chuyển đổi thành 1 đối tượng duy nhất

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