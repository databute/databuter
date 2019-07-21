package databute.databuter.cluster.coordinator;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class RemoteClusterNodeGroup {

    private static final Logger logger = LoggerFactory.getLogger(RemoteClusterNodeGroup.class);

    private final Map<String, RemoteClusterNode> remoteNodes;

    public RemoteClusterNodeGroup() {
        this.remoteNodes = Maps.newConcurrentMap();
    }

    public boolean add(RemoteClusterNode remoteNode) {
        checkNotNull(remoteNode, "remoteNode");

        final boolean added = (remoteNodes.putIfAbsent(remoteNode.id(), remoteNode) == null);
        if (added) {
            if (logger.isDebugEnabled()) {
                logger.debug("Added remote cluster node {}", remoteNode);
            } else {
                logger.info("Added remote cluster node {}", remoteNode.id());
            }
        }

        return added;
    }
}