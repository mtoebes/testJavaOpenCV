package team2901.javafx;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> Application.launch(TestJavaFXApplication.class)).start();
        TestJavaFXApplication testJavaFXApplication = TestJavaFXApplication.waitForStartUpTest();
        testJavaFXApplication.printSomething();
    }
}
