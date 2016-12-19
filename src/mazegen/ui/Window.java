// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends AbstractWindow {

    @Override
    public void show(Stage stage) {
        stage.setTitle(getTitle());
        stage.setWidth(getDefaultWidth());
        stage.setHeight(getDefaultHeight());

        stage.setScene(getScene());

        stage.show();
    }

    public String getTitle() {
        return "Window";
    }

    public int getDefaultWidth() {
        return 800;
    }

    public int getDefaultHeight() {
        return 600;
    }

    public Scene getScene() {
        return new Scene(new Group());
    }

}
