package mazegen;

import mazegen.ui.DuplicatingWindow;
import mazegen.ui.Launcher;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Launcher.openInitial(new DuplicatingWindow());
    }

}
