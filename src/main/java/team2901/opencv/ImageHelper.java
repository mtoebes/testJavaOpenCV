package team2901.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ImageHelper {
    
    public static Mat getChannelMat(Mat originalImage, int channel) {
        List<Mat> planes = new ArrayList<>();
        Core.split(originalImage, planes);
        return planes.get(channel);
    }
}
