package com.example.architecture.controllers;

import com.example.architecture.ArchitectureFX;
import com.example.architecture.messages.ExperimentResponse;
import com.example.architecture.model.DeviceStat;
import com.example.architecture.model.SourceStat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;

@Data
public class ResultController {

    @FXML
    public Button Graphics;
    @FXML
    public Button New;

    private ObservableList<SourceStat> listSource = FXCollections.observableArrayList();
    private ObservableList<DeviceStat> listDevice = FXCollections.observableArrayList();

    @FXML
    public Label result;

    @FXML
    public TableView<DeviceStat> device;
    @FXML
    public TableView<SourceStat> source;

    @FXML
    public TableColumn<SourceStat, String> numberSource;
    @FXML
    public TableColumn<SourceStat, Integer> N;
    @FXML
    public TableColumn<SourceStat, Double> P_otk;
    @FXML
    public TableColumn<SourceStat, Double> T_preb;
    @FXML
    public TableColumn<SourceStat, Double> T_BP;
    @FXML
    public TableColumn<SourceStat, Double> T_obsl;
    @FXML
    public TableColumn<SourceStat, Double> D_BP;
    @FXML
    public TableColumn<SourceStat, Double> D_obsl;

    @FXML
    public TableColumn<DeviceStat, String> numberDevice;
    @FXML
    public TableColumn<DeviceStat, Double> coeff;

    private ArchitectureFX architectureFX;
    private ExperimentResponse response;


    @FXML
    public void showResults() {
        result.setText(response.getLabel());
        initData();
        numberSource.setCellValueFactory(new PropertyValueFactory<>("number"));
        N.setCellValueFactory(new PropertyValueFactory<>("countReq"));
        P_otk.setCellValueFactory(new PropertyValueFactory<>("p_otk"));
        T_preb.setCellValueFactory(new PropertyValueFactory<>("t_preb"));
        T_BP.setCellValueFactory(new PropertyValueFactory<>("t_BP"));
        T_obsl.setCellValueFactory(new PropertyValueFactory<>("t_obsl"));
        D_BP.setCellValueFactory(new PropertyValueFactory<>("d_BP"));
        D_obsl.setCellValueFactory(new PropertyValueFactory<>("d_obsl"));

        numberDevice.setCellValueFactory(new PropertyValueFactory<>("number"));
        coeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));

        source.setItems(listSource);
        device.setItems(listDevice);

    }

    public ResultController(ArchitectureFX architectureFX) {
        this.architectureFX = architectureFX;
        response = architectureFX.getExperimentResponse();
    }

    private void initData() {
        for (int i = 0; i < response.getAnsSource().length; i++) {
            listSource.add(new SourceStat("И" + i, (int) response.getAnsSource()[i][0],
                    response.getAnsSource()[i][1], response.getAnsSource()[i][2], response.getAnsSource()[i][3],
                    response.getAnsSource()[i][4], response.getAnsSource()[i][5], response.getAnsSource()[i][6]));
        }
        for (int i = 0; i < response.getAnsDevice().length; i++) {
            listDevice.add(new DeviceStat("П" + i, response.getAnsDevice()[i]));
        }
    }

    @FXML
    public void newExperiment() {
        architectureFX.createParametersWindow();
    }

    @FXML
    public void createGraphics() {
        architectureFX.graphicsWindow();
    }

    @FXML
    public void showSteps() {
        architectureFX.showStepsWindow();
    }

}
