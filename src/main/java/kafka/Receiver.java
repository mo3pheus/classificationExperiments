package kafka;


import com.google.protobuf.InvalidProtocolBufferException;
import computer.vision.FaceRecognition;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ImageProcessingUtil;
import util.ImageUtil;
import util.TrackedLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author sanketkorgaonkar
 */
public class Receiver extends Thread {
    public final static String CHANNEL_PROPERTY = "source.topic";

    private          TrackedLogger     logger            = new TrackedLogger(Receiver.class);
    private          ConsumerConnector consumerConnector = null;
    private          long              lastReportTime    = 0l;
    private          long              radioCheckPulse   = 0l;
    private          String            tunedChannel      = "";
    private          BufferedImage     image             = null;
    private volatile boolean           runThread         = true;

    public Receiver(Properties comsConfig) {
        super("radio");
        ConsumerConfig consumerConfig = new ConsumerConfig(comsConfig);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        this.lastReportTime = System.currentTimeMillis();
        this.tunedChannel = comsConfig.getProperty(CHANNEL_PROPERTY);
    }

    public void setRadioCheckPulse(long radioCheckPulse) {
        this.radioCheckPulse = radioCheckPulse;
    }

    @Override
    public void run() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(tunedChannel, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
                .createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]>      stream = consumerMap.get(tunedChannel).get(0);
        ConsumerIterator<byte[], byte[]> it     = stream.iterator();

        while (it.hasNext() && runThread) {
            long timeElapsed = System.currentTimeMillis() - this.lastReportTime;
            logger.info("Time Elapsed since last message = " + timeElapsed);
            if (timeElapsed > this.radioCheckPulse) {
                this.lastReportTime = System.currentTimeMillis();
            }

            try {
                BufferedImage imag = ImageIO.read(new ByteArrayInputStream(it.next().message()));
                ImageProcessingUtil.writeImageToFile(imag, "inputImage.jpg");
//                JFrame frame = new JFrame();
//                frame.setBounds(0, 0, 1000, 1000);
//                frame.getContentPane().add(new ImageUtil(imag));
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                frame.setVisible(true);
//                Thread.sleep(10000);


                FaceRecognition faceRecognition = new FaceRecognition("output");
                faceRecognition.run("inputImage.jpg");
                JFrame classifiedFrame = new JFrame();
                classifiedFrame.setBounds(0, 0, 2000, 2000);
                classifiedFrame.getContentPane().add(new ImageUtil(ImageIO.read(new File("output/faceDetection.png"))));
                classifiedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                classifiedFrame.setVisible(true);
                Thread.sleep(5000);
                classifiedFrame.dispose();
                //frame.dispose();

            } catch (InvalidProtocolBufferException e) {
                logger.error("", e);
            } catch (InterruptedException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        logger.info("Radio receiver stopped.");
    }

    public void stopReceiver() {
        runThread = false;
    }

    private void getImage(Path location) {
        try {
            image = ImageIO.read(location.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

