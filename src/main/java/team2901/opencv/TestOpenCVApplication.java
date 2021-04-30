package team2901.opencv;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
 * Run from team2901.opencv.Main to avoid "JavaFX runtime components are missing, and are required to run this application"
 * https://stackoverflow.com/questions/25873769/launch-javafx-application-from-another-class
 *
 * or create module-info
 * https://stackoverflow.com/questions/52578072/gradle-openjfx11-error-javafx-runtime-components-are-missing
 */

public class TestOpenCVApplication extends Application {

    public static final String SAMPLE_IMAGES_DIR = "src" + File.separator + "sample-images" + File.separator;

    public static final CountDownLatch latch = new CountDownLatch(1);
    public static TestOpenCVApplication testJavaFXApplication = null;

    public static TestOpenCVApplication waitForStartUpTest() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return testJavaFXApplication;
    }

    public static void setStartUpTest(TestOpenCVApplication testJavaFXApplication0) {
        testJavaFXApplication = testJavaFXApplication0;
        latch.countDown();
    }

    public TestOpenCVApplication() {
        setStartUpTest(this);
    }

    public void printSomething() {
        System.out.println("You called a method on the application");
    }

    @Override
    public void start(Stage stage) throws Exception {

        String fileName = SAMPLE_IMAGES_DIR + "open-cv-logo.png";

        Group originalImageGroup = loadImageIntoGroup(fileName, null);
        Group plane0ImageGroup = loadImageIntoGroup(fileName, 0);
        Group plane1ImageGroup = loadImageIntoGroup(fileName, 1);
        Group plane2ImageGroup = loadImageIntoGroup(fileName, 2);

        GridPane gridPane = new GridPane();
        gridPane.add(originalImageGroup, 0, 0);
        gridPane.add(plane0ImageGroup, 1, 0);
        gridPane.add(plane1ImageGroup, 0, 1);
        gridPane.add(plane2ImageGroup, 1, 1);

        Scene scene = new Scene(gridPane);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


    public Group loadImageIntoGroup(Mat image) throws IOException {

        Rect rect = new Rect();

        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);

        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();

        //Displaying the image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);

        System.out.println("Image Loaded");
        WritableImage writableImage = SwingFXUtils.toFXImage(bufImage, null);

        ImageView imageView1 = new ImageView(writableImage);
        imageView1.setX(0);
        imageView1.setY(rect.height);
        imageView1.setFitHeight(image.height());
        imageView1.setFitWidth(image.width());
        imageView1.setPreserveRatio(true);
        Group imageGroup = new Group(imageView1);
        rect.height += image.height();
        rect.width = image.width();

        return imageGroup;
    }

    public Group loadImageIntoGroup(String filename, Integer index) throws IOException {

        nu.pattern.OpenCV.loadLocally();

        //Reading the Image from the file and storing it in to a Matrix object
        Mat image = Imgcodecs.imread(filename);

        List<Mat> planes = new ArrayList<>();
        Core.split(image, planes);

        if (index == null) {
            return loadImageIntoGroup(image);
        } else {
            return loadImageIntoGroup(planes.get(index));
        }
    }
}
