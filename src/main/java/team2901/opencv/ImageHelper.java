package team2901.opencv;

import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageHelper {

    public static Mat getChannelMat(Mat originalImage, int channel) {
        List<Mat> planes = new ArrayList<>();
        Core.split(originalImage, planes);
        return planes.get(channel);
    }

    public static Mat getGrayscaleMat(Mat originalImage) {
        Mat image = new Mat();

        // Converting the image to gray scale and
        // saving it in the dst matrix
        Imgproc.cvtColor(originalImage, image, Imgproc.COLOR_RGB2GRAY);

        return image;
    }
}
