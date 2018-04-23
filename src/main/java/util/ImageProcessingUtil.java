package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessingUtil {
    private static TrackedLogger logger = new TrackedLogger(ImageProcessingUtil.class);

    public static void writeImageToFile(BufferedImage bufferedImage, String dataArchivePath) {
        if (bufferedImage == null) {
            throw new RuntimeException("BufferedImage was null");
        }

        String fileName   = dataArchivePath;
        File   outputFile = new File(fileName);
        logger.debug("CameraUtil trying to write the following fileName = " + fileName);
        try {
            ImageIO.write(bufferedImage, "jpg", outputFile);
        } catch (IOException io) {
            logger.error("Cant write the following file =>" + fileName, io);
        }
    }
}
