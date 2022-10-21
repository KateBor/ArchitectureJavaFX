package com.example.architecture.controllers;

import com.example.architecture.ArchitectureFX;
import com.example.architecture.messages.EventResponse;
import com.example.architecture.model.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class StepsController {

    private Stage dialogStage;
    private ArchitectureFX architectureFX;
    private EventResponse response;
    private ObservableList<Event> listSteps = FXCollections.observableArrayList();

    @FXML
    public TableView<Event> steps;

    @FXML
    public TableColumn<Event, String> source;
    @FXML
    public TableColumn<Event, String> device;
    @FXML
    public TableColumn<Event, String> buffer;
    @FXML
    public TableColumn<Event, Double> reqNum;
    @FXML
    public TableColumn<Event, Double> time;
    @FXML
    public TableColumn<Event, String> event;
    @FXML
    public TableColumn<Event, Integer> countRequests;
    @FXML
    public TableColumn<Event, Integer> countRejection;

    public StepsController(ArchitectureFX architectureFX) {
        this.architectureFX = architectureFX;
        response = architectureFX.getEventResponse();
    }

    @FXML
    public void showSteps() {
        listSteps.addAll(response.getEventList());
        source.setCellValueFactory(new PropertyValueFactory<>("source"));
        device.setCellValueFactory(new PropertyValueFactory<>("device"));
        buffer.setCellValueFactory(new PropertyValueFactory<>("buffer"));
        reqNum.setCellValueFactory(new PropertyValueFactory<>("reqNum"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        event.setCellValueFactory(new PropertyValueFactory<>("event"));
        countRequests.setCellValueFactory(new PropertyValueFactory<>("countReq"));
        countRejection.setCellValueFactory(new PropertyValueFactory<>("countReject"));

        steps.setItems(listSteps);
    }
}
