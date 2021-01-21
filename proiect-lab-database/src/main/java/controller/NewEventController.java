package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Event;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.*;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class NewEventController {
    public DatePicker datepick;
    public TextField namefield;
    public TextField locationfield;
    public Spinner hourpick;
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Utilizator user;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public void setUser(Utilizator user) {
        this.user=user;
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService, MessageService messagesService, RequestService friendRequestsService, EventService eventService) {
        this.utilizatorService= utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
    }
    public void initialize(){
        datepick.setValue(LocalDate.from(LocalDateTime.now()));
        hourpick.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,24));
    }

    public void handlecreate(ActionEvent actionEvent) {
        String err="";
        String name = namefield.getText();
        String loc = locationfield.getText();
        if(name.equals(""))
            err+="Invalid name!\n";
        if(loc.equals(""))
            err+="Invalid location!\n";
        LocalDate date = datepick.getValue();
        int hour = (Integer) hourpick.getValue();
        LocalTime time  = LocalTime.of(hour,0,0,0);
        LocalDateTime dateTime = time.atDate(date);
        if(dateTime.isBefore(LocalDateTime.now()))
            err+="Invalid date!\n";
        if(err==""){
            Event newEvent = new Event(name,dateTime,user.getId(),loc);
            eventService.save(newEvent);
        }
        else{
            AlertBox alertBox = new AlertBox("Error", err);
            alertBox.display();
        }
    }

    public void handleback(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/UserMenu.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);

        UserMenuController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService,friendshipsService,messagesService,friendRequestsService, eventService);
        primaryStage.setTitle("User Menu");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }
}
