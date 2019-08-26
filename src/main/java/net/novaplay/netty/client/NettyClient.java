package net.novaplay.netty.client;

import net.novaplay.callback.Callback;
import net.novaplay.netty.packet.PacketDecoder;
import net.novaplay.netty.packet.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    public static final boolean EPOLL = Epoll.isAvailable();
    public static ExecutorService pool = Executors.newCachedThreadPool();

    @Getter
    private EventLoopGroup eventLoopGroup;
    @Getter
    private Bootstrap bootstrap;
    @Getter
    private ChannelFuture future;

    @Getter
    @Setter
    private String host = "localhost";

    @Getter
    @Setter
    private int port = 8000;

    @Getter
    @Setter
    private Channel channel;
    
    public void setChannel(Channel c) {
    	channel = c;
    }
    
    public Channel getChannel() {
    	return channel;
    }
    
    public void setHost(String h) {
    	host = h;
    }
    
    public String getHost() {
    	return host;
    }
    
    public void setPort(int h) {
    	port = h;
    }
    
    public int getPort() {
    	return port;
    }
    
    public ChannelFuture getFuture() {
    	return future;
    }

    public NettyClient() {
        //Logger.log( "connecting to netty" );
    }

    public void connect(String host, int port, Callback callback) {
        setHost(host);
        setPort(port);
        pool.execute(() -> {
            EventLoopGroup eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            try {
                bootstrap = new Bootstrap()
                        .group(eventLoopGroup)
                        .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline().addLast(new PacketEncoder());
                                channel.pipeline().addLast(new PacketDecoder());
                                channel.pipeline().addLast(new ClientHandler(NettyClient.this));
                            }
                        } );
                future = bootstrap.connect(host, port);
                callback.accept();
                future.sync().channel().closeFuture().syncUninterruptibly();
            } catch (Exception e) {
                channel = null;
                System.out.println("failed connecting to netty: " + e.getMessage());
                System.out.println("reconnecting in 5 seconds");
                scheduleConnect(5000);
            } finally {
                eventLoopGroup.shutdownGracefully();
                channel = null;
            }
        } );
    }

    public void scheduleConnect(int time) {
        if(isConnected()){
            return;
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                    	System.out.println("reconnecting...");
                        connect(getHost(), getPort(), new Callback() {
                            @Override
                            public void accept(Object... args) {
                            }
                        } );
                    }
                },
                time
        );
    }

    public void disconnect() {
        try {
            if(getFuture() == null || getChannel() == null) {
                return;
            }
            if(!getChannel().isActive()) {
                return;
            }
            getChannel().close();
            channel = null;
            future = null;
            bootstrap = null;
            eventLoopGroup = null;
        } catch ( Exception ignored ) {
        }
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}