package team2901.opencv;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public static final String DEFAULT_IMAGE = SAMPLE_IMAGES_DIR + "open-cv-logo.png";

    public static final CountDownLatch latch = new CountDownLatch(1);
    public static TestOpenCVApplication testJavaFXApplication = null;

    private Stage stage;

    private ImageView transformImageView;

    private Mat originalImage;
    private Mat editImage;

    private Transform selectedTransform = Transform.ORIGINAL;

    public static void main(String[] args) {
        Application.launch(args);
    }

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

    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;

        nu.pattern.OpenCV.loadLocally();

        // Build Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(buildFileMenu());
        menuBar.getMenus().add(buildTransformMenu());

        // Build image views
        transformImageView = new ImageView();
        Group editedImageGroup = new Group(transformImageView);

        // Display
        VBox vBox = new VBox(menuBar, editedImageGroup);
        Scene scene = new Scene(vBox);

        stage.setScene(scene);
        stage.show();

        loadImage(DEFAULT_IMAGE);
    }

    public Menu buildFileMenu() {

        final Menu fileMenu = new Menu("File");

        final FileChooser fileChooser = new FileChooser();
        final File file = new File(SAMPLE_IMAGES_DIR);
        if (!file.exists()) {
            file.mkdir();
        }

        fileChooser.setInitialDirectory(file);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));

        // Open image menu item
        final MenuItem open = new MenuItem("Open");
        fileMenu.getItems().add(open);
        open.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            try {
                if (selectedFile != null) {
                    loadImage(selectedFile.getPath());
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        // Save image menu item
        final MenuItem save = new MenuItem("Save");
        fileMenu.getItems().add(save);
        save.setOnAction(e -> {
            File selectedFile = fileChooser.showSaveDialog(stage);
            try {
                if (selectedFile != null) {
                    writeImage(selectedFile.getPath());
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        return fileMenu;
    }

    public Menu buildTransformMenu() {

        final Menu editMenu = new Menu("Transform");

        final ToggleGroup toggleGroup = new ToggleGroup();

        editMenu.getItems().add(buildTransformMenuItem("Original",  toggleGroup, Transform.ORIGINAL));

        final Menu menuEffect = new Menu("RGB Channel");
        menuEffect.getItems().add(buildTransformMenuItem("Red",  toggleGroup, Transform.RED_CHANNEL));
        menuEffect.getItems().add(buildTransformMenuItem("Green",  toggleGroup, Transform.GREEN_CHANNEL));
        menuEffect.getItems().add(buildTransformMenuItem("Blue",  toggleGroup, Transform.BLUE_CHANNEL));
        menuEffect.getItems().add(buildTransformMenuItem("Gray",  toggleGroup, Transform.GRAY_CHANNEL));

        editMenu.getItems().addAll(menuEffect);

        return editMenu;
    }

    private RadioMenuItem buildTransformMenuItem(final String name, final ToggleGroup toggleGroup, final Transform transform) {

        final RadioMenuItem menuItem = new RadioMenuItem(name);
        menuItem.setToggleGroup(toggleGroup);
        menuItem.setSelected(transform == selectedTransform);
        menuItem.setOnAction(e -> {
            try {
                setSelectedTransform(transform);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        return menuItem;
    }

    public void setSelectedTransform(Transform selectedTransform) throws IOException {
        this.selectedTransform = selectedTransform;
        performTransform();
    }

    public void loadImage(String filePath) throws IOException {
        originalImage = Imgcodecs.imread(filePath);
        performTransform();
    }

    public void writeImage(String filePath) throws IOException {
        Imgcodecs.imwrite(filePath, editImage);
    }

    public void performTransform() throws IOException {

        if (originalImage == null) {
            return;
        }

        editImage = getTransformedImage(originalImage, selectedTransform);

        displayImage(editImage, transformImageView);

        stage.sizeToScene() ;
    }

    public void displayImage(Mat image, ImageView imageView) throws IOException {

        Rect rect = new Rect();

        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);

        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();

        //Displaying the image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);

        WritableImage writableImage = SwingFXUtils.toFXImage(bufImage, null);

        imageView.setImage(writableImage);
        imageView.setX(0);
        imageView.setY(rect.height);
        imageView.setFitHeight(image.height());
        imageView.setFitWidth(image.width());
        imageView.setPreserveRatio(true);
        rect.height += image.height();
        rect.width = image.width();
    }

    public Mat getTransformedImage(Mat originalImage, Transform selectedTransform) {

        if (selectedTransform == null) {
            return originalImage;
        }

        switch (selectedTransform) {
            case BLUE_CHANNEL:
                return ImageHelper.getChannelMat(originalImage, 0);
            case GREEN_CHANNEL:
                return ImageHelper.getChannelMat(originalImage, 1);
            case RED_CHANNEL:
                return ImageHelper.getChannelMat(originalImage, 2);
            case GRAY_CHANNEL:
                return ImageHelper.getGrayscaleMat(originalImage);
            case ORIGINAL:
            default:
                return originalImage;
        }
    }
}
