package databute.databuter.cluster.network;

import databute.databuter.cluster.Cluster;
import databute.databuter.cluster.coordinator.RemoteClusterNode;
import databute.databuter.cluster.handshake.request.HandshakeRequestMessage;
import databute.databuter.cluster.handshake.response.HandshakeResponseMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class OutboundClusterChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(OutboundClusterChannelHandler.class);

    private ClusterSession session;

    private final Cluster cluster;
    private final RemoteClusterNode remoteNode;

    public OutboundClusterChannelHandler(Cluster cluster, RemoteClusterNode remoteNode) {
        this.cluster = checkNotNull(cluster, "cluster");
        this.remoteNode = checkNotNull(remoteNode, "remoteNode");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final SocketChannel channel = (SocketChannel) ctx.channel();
        session = new ClusterSession(channel, cluster);
        logger.info("Active new cluster outbound session {}", session);

        remoteNode.session(session);

        configurePipeline(ctx);

        session.send(new HandshakeRequestMessage(remoteNode.id()));
    }

    private void configurePipeline(ChannelHandlerContext ctx) {
        final ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast(new HandshakeResponseMessageHandler(session));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("Inactive cluster outbound session {}", session);

        cluster.remoteNodeGroup().remove(remoteNode);
    }
}
