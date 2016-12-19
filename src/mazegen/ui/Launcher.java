// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static AbstractWindow initialWindow;

    public static void openInitial(AbstractWindow window) {
        initialWindow = window;
        Application.launch();
    }

    public static void open(AbstractWindow window) {
        window.show(new Stage());
    }

    @Override
    public void start(Stage stage) {
        initialWindow.show(stage);
    }

}
