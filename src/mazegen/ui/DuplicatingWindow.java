// Copyright (c) 2016 by Radon Rosborough. All rights reserved.
package mazegen.ui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class DuplicatingWindow extends Window {

    @Override
    public Scene getScene() {
        Button regularButton = new Button();
        regularButton.setText("Open Regular Window");
        regularButton.setOnAction(event -> Launcher.open(new Window()));

        Button duplicatingButton = new Button();
        duplicatingButton.setText("Open Duplicating Window");
        duplicatingButton.setOnAction(event -> Launcher.open(new DuplicatingWindow()));

        TilePane root = new TilePane(Orientation.VERTICAL, regularButton, duplicatingButton);
        root.setAlignment(Pos.CENTER);
        root.setPrefTileHeight(50);

        return new Scene(root);
    }
    
}
