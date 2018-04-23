package computer.vision;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.image.BufferedImage;

public class FaceRecognition {

    private String          imageLocation = null;
    private BufferedImage[] imageLibrary  = null;

    public FaceRecognition(String imageLocation) {
        this.imageLocation = imageLocation;
    }


    public String run(String fileName) {
        OpenCV.loadShared();
        CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml")
                                                                       .getPath());
        Mat image = Imgcodecs.imread(fileName);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect
                    .height), new Scalar(0, 255, 0));
        }
        String targetFile = "output/faceDetection.png";
        Imgcodecs.imwrite(targetFile, image);
        return targetFile;
    }
}