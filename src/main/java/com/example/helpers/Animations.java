package com.example.helpers;

import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class Animations {
    static public void showControll(Control control, int millisSleep) {
        new Thread(() -> {
            control.setVisible(true);
            control.setOpacity(1);
            try {
                Thread.sleep(millisSleep);
                while (control.getOpacity() >= 0) {
                    control.setOpacity(control.getOpacity() - 0.05);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            control.setVisible(false);
        }).start();
    }

    static public void showControll(Control control, int millisSleep, String textColor) {
        new Thread(() -> {
            control.setStyle("-fx-text-fill: " + textColor);
            control.setVisible(true);
            control.setOpacity(1);
            try {
                Thread.sleep(millisSleep);
                while (control.getOpacity() >= 0) {
                    control.setOpacity(control.getOpacity() - 0.05);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            control.setVisible(false);
        }).start();
    }

    static public void showSlideBottom(Pane pane) {
        double finish = 0;
        new Thread(() -> {
            while (AnchorPane.getBottomAnchor(pane) < finish) {
                AnchorPane.setBottomAnchor(pane, AnchorPane.getBottomAnchor(pane) - (AnchorPane.getBottomAnchor(pane) - finish) / 6. + 1);
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnchorPane.setBottomAnchor(pane,0.);
        }).start();
    }

    static public void hideSlideBottom(Pane pane) {
        double finish = -pane.getHeight();
        new Thread(() -> {
            while (AnchorPane.getBottomAnchor(pane) > finish) {
                AnchorPane.setBottomAnchor(pane, AnchorPane.getBottomAnchor(pane) - (AnchorPane.getBottomAnchor(pane) - finish) / 6. - 1);
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnchorPane.setBottomAnchor(pane, -pane.getHeight());
        }).start();
    }


    static public void showSlideRight(Pane pane) {
        double finish = 0;
        new Thread(() -> {
            while (AnchorPane.getRightAnchor(pane) < finish) {
                AnchorPane.setRightAnchor(pane, AnchorPane.getRightAnchor(pane) - (AnchorPane.getRightAnchor(pane) - finish) / 6. + 1);
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnchorPane.setRightAnchor(pane,0.);
        }).start();
    }

    static public void hideSlideRight(Pane pane) {
        double finish = -pane.getWidth();
        new Thread(() -> {
            while (AnchorPane.getRightAnchor(pane) > finish) {
                AnchorPane.setRightAnchor(pane, AnchorPane.getRightAnchor(pane) - (AnchorPane.getRightAnchor(pane) - finish) / 6. - 1);
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AnchorPane.setRightAnchor(pane, -pane.getWidth());
        }).start();
    }
}
