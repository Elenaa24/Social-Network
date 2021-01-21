package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class EventsController {
    public TableView<EventDTO> tableEvent;
    public TableColumn nameColumn;
    public TableColumn hostColumn;
    public TableColumn dateColumn;
    public TableColumn statusColumn;
    public TableColumn participantsColumn;
    public Button unsubscribe;
    public Button going;
    public Button subscribe;
    public Button notgoing;
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Utilizator user;
    ObservableList<EventDTO> events = FXCollections.observableArrayList();
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
        events.setAll(getEvents());
    }
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<EventDTO,String>("nume"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<EventDTO,String>("organizator"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<EventDTO,String>("data"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<EventDTO,String>("status"));
        participantsColumn.setCellValueFactory(new PropertyValueFactory<EventDTO,String>("participanti"));
        tableEvent.setItems(events);
    }

    private List<EventDTO> getEvents() {
        List<Event> event = eventService.getAll();
        List<EventDTO> all = event.stream()
                .filter(x->ChronoUnit.DAYS.between(LocalDateTime.now(),x.getDate())>=0)
                .map(x->{
                    Utilizator a = utilizatorService.findOne(x.getOrganizator()).get();
                    String host="";
                    host = a.getFirstName()+" "+a.getLastName();
                    DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String data = x.getDate().format(myFormat);
                    List<Tuple<Long, Boolean>> participanti = x.getParticipanti();
                    String participants = "";
                    for (Tuple<Long, Boolean> longBooleanTuple : participanti) {
                        Utilizator participant = utilizatorService.findOne(longBooleanTuple.getLeft()).get();
                        participants+= participant.getFirstName()+" "+participant.getLastName()+",";
                    }
                    String status;
                    if(participanti.contains(new Tuple<>(user.getId(),true)))
                        status = "SUBSCRIBER";
                    else {
                        if (participanti.contains(new Tuple<>(user.getId(), false)))
                            status = "GOING";
                        else
                            status = "NOT GOING";
                    }
                    EventDTO eventDTO = new EventDTO(host,participants,status,data,x.getName(),x.getId());
                    return eventDTO;
                })
                .collect(Collectors.toList());
        return all;
    }

    public void handlerSelectEvent(MouseEvent mouseEvent) {
        unsubscribe.setDisable(false);
        subscribe.setDisable(false);
        going.setDisable(false);
        notgoing.setDisable(false);
        EventDTO eventDTO = tableEvent.getSelectionModel().getSelectedItem();
        Event event = eventService.findOne(eventDTO.getId());
        if(event.getOrganizator()== user.getId()) {
            unsubscribe.setDisable(true);
            subscribe.setDisable(true);
            going.setDisable(true);
            notgoing.setDisable(true);
        }
        else {
            if(event.getParticipanti().contains(new Tuple<>(user.getId(),true))) {
                subscribe.setDisable(true);
                going.setDisable(true);
            }
            else{
                if(event.getParticipanti().contains(new Tuple<>(user.getId(),false))) {
                    unsubscribe.setDisable(true);
                    going.setDisable(true);
                }
                else{
                    unsubscribe.setDisable(true);
                    notgoing.setDisable(true);
                }
            }
        }

    }

    public void handlerUnsubscribe(ActionEvent actionEvent) {
        EventDTO eventDTO = tableEvent.getSelectionModel().getSelectedItem();
        Event event = eventService.findOne(eventDTO.getId());
        List<Tuple<Long, Boolean>> participants = event.getParticipanti();
        participants.remove(new Tuple<>(user.getId(),true));
        event.addParticipant(user.getId(),false);
        eventService.updateEvent(event);
        events.setAll(getEvents());
    }

    public void handlerGoing(ActionEvent actionEvent) {
        EventDTO eventDTO = tableEvent.getSelectionModel().getSelectedItem();
        Event event = eventService.findOne(eventDTO.getId());
        event.addParticipant(user.getId(),false);
        eventService.updateEvent(event);
        events.setAll(getEvents());
    }

    public void handlerSubscribe(ActionEvent mouseEvent) {
        System.out.println("OK");
        EventDTO eventDTO = tableEvent.getSelectionModel().getSelectedItem();
        System.out.println(eventDTO);
        Event event = eventService.findOne(eventDTO.getId());

        List<Tuple<Long, Boolean>> participants = event.getParticipanti();
        System.out.println(participants);
        System.out.println(participants.remove(new Tuple<>(user.getId(), false)));
        System.out.println(participants.add(new Tuple<>(user.getId(), true)));
        event.setParticipanti(participants);
        System.out.println(event);
        eventService.updateEvent(event);
        events.setAll(getEvents());
    }

    public void handlerNotGoing(ActionEvent actionEvent) {
        EventDTO eventDTO = tableEvent.getSelectionModel().getSelectedItem();
        Event event = eventService.findOne(eventDTO.getId());
        List<Tuple<Long, Boolean>> participants = event.getParticipanti();
        System.out.println(participants);
        participants.removeIf(x->x.getLeft()== user.getId());
        System.out.println(participants);
        event.setParticipanti(participants);
        eventService.updateEvent(event);
        events.setAll(getEvents());
    }

    public void handlerBack(ActionEvent actionEvent) throws IOException {
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
