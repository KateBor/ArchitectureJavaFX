package com.example.architecture.controllers;

import com.example.architecture.ArchitectureFX;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Data;

@Data
public class ArchController {
    public Label welcomeText;
    private ArchitectureFX architectureFX;

    @FXML
    public void onStartButtonClick() {
        architectureFX.createParametersWindow();
    }
}