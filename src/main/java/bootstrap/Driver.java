package bootstrap;

import communications.protocol.KafkaConfig;
import kafka.Receiver;
import util.LoggerUtil;
import util.TrackedLogger;
import util.UUIdSingleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Driver {
    public static final String SEPARATOR =
            "==============================================================";

    public static Properties    projectProperties = new Properties();
    public static TrackedLogger logger            = new TrackedLogger(Driver.class);

    public static void main(String[] args) {
        try {
            LoggerUtil.configureConsoleLogging(Boolean.parseBoolean(args[0]), UUIdSingleton
                    .getInstance().uuid);
            logger.info(SEPARATOR);
            projectProperties = getProjectProperties(args[1]);
            Receiver receiver = new Receiver(KafkaConfig.getTSConfig("receiver"));
            receiver.setRadioCheckPulse(500);
            receiver.start();
            logger.info(SEPARATOR);
        } catch (IOException io) {
            logger.error("Error while reading the project properties file.", io);
        }
    }

    public static Properties getProjectProperties(String propertiesFilePath) throws IOException {
        logger.info("Properties file specified at location = " + propertiesFilePath);
        FileInputStream projFile   = new FileInputStream(propertiesFilePath);
        Properties      properties = new Properties();
        properties.load(projFile);
        return properties;
    }
}
