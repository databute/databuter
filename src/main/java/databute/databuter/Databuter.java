package databute.databuter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import databute.databuter.bucket.Bucket;
import databute.databuter.bucket.BucketException;
import databute.databuter.bucket.BucketGroup;
import databute.databuter.cluster.Cluster;
import databute.databuter.cluster.ClusterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public final class Databuter {

    private static final Logger logger = LoggerFactory.getLogger(Databuter.class);
    private static final Databuter instance = new Databuter();
    private static final BucketGroup bucketGroup = new BucketGroup();

    private DatabuterConfiguration configuration;
    private Cluster cluster;

    private Databuter() {
        super();
    }

    public static Databuter instance() {
        return instance;
    }

    private void start() throws IOException, ClusterException, BucketException {
        logger.info("Starting Databuter at {}", Instant.now());

        loadConfiguration();

        makeBucket();

        joinCluster();
    }

    private void loadConfiguration() throws IOException {
        logger.debug("Loading configuration...");

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final Path configurationPath = Paths.get(DatabuterConstants.CONFIGURATION_PATH);
        final File configurationFile = configurationPath.toFile();
        configuration = mapper.readValue(configurationFile, DatabuterConfiguration.class);
        logger.info("Loaded configuration from {}", configurationPath.toAbsolutePath());
        logger.debug("Loaded configuration: {}", configuration);
    }

    private void joinCluster() throws ClusterException {
        logger.debug("Joining cluster...");

        cluster = new Cluster(configuration.cluster(), bucketGroup);
        cluster.join();
    }

    private void makeBucket() throws BucketException {
        final long availableMemory = Runtime.getRuntime().totalMemory() - configuration.guardMemorySizeMb();
        final long bucketCount = availableMemory / configuration.bucketMemorySizeMb();

        logger.debug("Making {} bucket...", bucketCount);

        for (int i = 0; i < bucketCount; ++i) {
            final Bucket bucket = new Bucket();
            final boolean added = bucketGroup.add(bucket);
            if (!added) {
                throw new BucketException("Found duplcated bucket " + bucket);
            }
        }
    }

    public static void main(String[] args) {
        try {
            instance().start();
        } catch (Exception e) {
            logger.error("Failed to start Databuter.", e);
        }
    }
}
