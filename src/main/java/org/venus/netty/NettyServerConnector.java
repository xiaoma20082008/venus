package org.venus.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venus.Container;
import org.venus.Mapper;
import org.venus.ServerConnector;
import org.venus.config.ServerConfig;
import org.venus.core.*;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServerConnector extends LifecycleBase implements ServerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerConnector.class);
    private final Mapper mapper;
    private final NettyConnectionManager manager;
    private ServerConfig config = new ServerConfig();
    private ChannelFuture future;
    private EventLoopGroup acceptor;
    private EventLoopGroup selector;
    private EventLoopGroup codec;
    private EventLoopGroup worker;
    private Container container;
    private Charset uriCharset;

    public NettyServerConnector() {
        this.mapper = new StandardMapper();
        this.manager = new NettyConnectionManager(config.getNetMaxConnections());
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Charset getURICharset() {
        return this.uriCharset;
    }

    @Override
    public String getURIEncoding() {
        return this.uriCharset.name();
    }

    @Override
    public void setURIEncoding(String encoding) {
        this.uriCharset = Charset.forName(encoding);
    }

    @Override
    public Mapper getMapper() {
        return this.mapper;
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) this.future.channel().localAddress();
    }

    @Override
    public void start() throws Exception {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
        NettyAdapter adapter = new NettyAdapter(this);

        StandardInStream inStream = new StandardInStream();
        StandardHostStream hostStream = new StandardHostStream();
        StandardOutStream outStream = new StandardOutStream();
        hostStream.setNext(outStream);
        inStream.setNext(hostStream);

        this.container = inStream;
        this.acceptor = new NioEventLoopGroup(1, new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("NettyAcceptor_" + idx.getAndIncrement());
                return t;
            }
        });
        this.selector = new NioEventLoopGroup(this.config.getThreadSelectorNums(), new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("NettySelector_" + config.getThreadSelectorNums() + "_" + idx.getAndIncrement());
                return t;
            }
        });
        this.codec = new NioEventLoopGroup(this.config.getThreadCodecNums(), new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("NettyCodec_" + config.getThreadCodecNums() + "_" + idx.getAndIncrement());
                return t;
            }
        });
        this.worker = new NioEventLoopGroup(this.config.getThreadWorkerNums(), new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("NettyWorker_" + config.getThreadWorkerNums() + "_" + idx.getAndIncrement());
                return t;
            }
        });

        ServerBootstrap server = new ServerBootstrap()
                .group(this.acceptor /* serverSocket.accept */, this.selector /* java.nio.channels.Selector.select() */)
                .localAddress(new InetSocketAddress(this.config.getPort()))
                .option(ChannelOption.SO_BACKLOG, this.config.getNetSoBacklog())
                .option(ChannelOption.SO_REUSEADDR, this.config.isNetSoReuseaddr())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getNetConnectTimeout())
                .childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(acceptor, "manager", new NettyConnectionHandler(manager));
                        channel.pipeline().addLast(selector, "codec", new HttpServerCodec());
                        channel.pipeline().addLast(selector, "aggregator", new HttpObjectAggregator(config.getNetMaxBytes()));
                        channel.pipeline().addLast(worker, "handler", new NettyRequestReceiver(adapter));
                    }
                });
        this.future = server.bind().sync();
        this.container.start();
        this.future.await();
        LOGGER.info("## connector started...");
    }

    @Override
    public void close() throws Exception {
        // 1. 不再接收新的请求
        this.acceptor.shutdownGracefully();
        // 2. 等待业务处理完成
        this.worker.shutdownGracefully();
        // 3. 等待编解码处理完成
        this.codec.shutdownGracefully();
        // 4. 清空所有Socket,会发送IO事件
        this.manager.closeAll();
        // 5. 关闭ServerSocketChannel,会发送IO事件
        this.future.channel().close().syncUninterruptibly();
        // 6. 等待所有的IO事件处理完成
        this.selector.shutdownGracefully();
        LOGGER.info("## connector closed...");
    }

}
