package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.repository.Status;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.repository.paging.Page;
import socialnetwork.service.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserMenuController {
    public Pagination page;
    private Stage primaryStage;
    //private MessengerController messengerController;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Utilizator user;
    ObservableList<Utilizator> modelGrade = FXCollections.observableArrayList();
    ObservableList<Request> model = FXCollections.observableArrayList();
    ObservableList<String> notificari = FXCollections.observableArrayList();
    @FXML
    TableColumn<Utilizator, String> nume;
    @FXML
    TableColumn<Utilizator, String> prenume;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    CheckBox checkfriend;
    @FXML
    TextField txt_search;
    @FXML
    TableView<Request> tableViewRequests;
    @FXML
    TableColumn<Request,String> nume_request;
    @FXML
    TableColumn<Request,String> prenume_request;
    @FXML
    TableColumn<Request,String> data_request;
    @FXML
    TableColumn<Request,String> status_request;
    @FXML
    ImageView image;
    @FXML
    TextField name;
    @FXML
    Button notificationsButton;
    @FXML
    ListView eventlist;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService) throws FileNotFoundException {
        this.utilizatorService = utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService=eventService;
        //modelGrade.setAll(getUsersList());
        model.setAll(getRequestsList());
        notificari.setAll(getNot());
        FileInputStream inputstream = new FileInputStream(user.getImage());
        Image image2 = new Image(inputstream);
        image.setImage(image2);
        name.setText(user.getLastName()+" "+user.getFirstName());
        List<Event> list = getEventsForUser();
        this.notificationsButton.setText(String.valueOf(list.size()));
        page.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                pageHandler());
        Page<Utilizator> users = utilizatorService.getPagedUsers(page.getCurrentPageIndex());
        modelGrade.setAll(users.getContent().collect(Collectors.toList()));
    }


    public void handleButtonBackToStart(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/startMenuView.fxml"));
        AnchorPane root = loader.load();
        Scene startScene = new Scene(root,700,500);

        StartMenuController startMenuController = loader.getController();
        startMenuController.setPrimaryStage(primaryStage);
        startMenuController.setServices(utilizatorService,friendshipsService,messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("My Social network");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public void initialize() throws FileNotFoundException {
        nume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        prenume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(modelGrade);
        txt_search.textProperty().addListener(x -> handleSearch());
        nume_request.setCellValueFactory(new PropertyValueFactory<Request, String>("fromName"));
        prenume_request.setCellValueFactory(new PropertyValueFactory<Request, String>("fromLastName"));
        status_request.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
        data_request.setCellValueFactory(new PropertyValueFactory<Request, String>("data"));
        tableViewRequests.setItems(model);
        eventlist.setItems(notificari);

    }

    private List<Utilizator> getUsersList() {
        // TODO
        List<Utilizator> userDtoList = new ArrayList<>();
        List<Utilizator> userList = utilizatorService.getAllUsers();

        return userList.stream()
                .map(x->{ return new Utilizator(x.getFirstName(),x.getLastName());}
                )
                .collect(Collectors.toList());
    }
    private List<Request> getRequestsList() {
       return friendRequestsService.getRequests(user);
    }
    private List<Event> getEvents(){
        return eventService.getAll();
    }
    private List<Event> getEventsForUser(){
        List<Event> eventss = eventService.getAll();
        if(eventss.isEmpty())
            return eventss;
        List<Event> list = eventss.stream()
                .filter(event -> (event.getOrganizator().equals(user.getId()) || event.getParticipanti().contains(new Tuple<>(user.getId(),true))) && event.getDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        return list;
    }
    private List<String> getNot(){
        return eventService.getNotifications(user);
    }

    public void handleButtonFriendCheck() {
        if(checkfriend.isSelected()) {
            page.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                    pageHandler());
            Page<Utilizator> utilizatorPage =  utilizatorService.getPagedFriendsOfUser(page.getCurrentPageIndex(), user.getId());
            modelGrade.setAll(utilizatorPage.getContent().collect(Collectors.toList()));
            tableView.setItems(modelGrade);
        }
        else
            pageHandler();
    }

    public void handleSearch() {
        String filter = txt_search.getText();
        List<Utilizator> list = getUsersList();
        modelGrade.setAll(StreamSupport.stream(getUsersList().spliterator(), false)
                .filter(x->x.getFirstName().contains(filter))
                .collect(Collectors.toList()));
    }

    public void handleButtonAddFriend(ActionEvent actionEvent) {
        Utilizator user1 = tableView.getSelectionModel().getSelectedItem();
        Utilizator user2 = utilizatorService.findd(user1.getFirstName(),user1.getLastName()).get();
        try {
            if(user1.equals(user)){
                AlertBox alertBox = new AlertBox("Error!", "Nu iti poti trimite o cerere!");
                alertBox.display();
            }
            else
                friendRequestsService.sendRequest(user, user2.getId());
            } catch (RepoException ex) {
                AlertBox alertBox = new AlertBox("Error!", ex.getMessage());
                alertBox.display();
            }

            model.setAll(getRequestsList());

    }

    public void handleButtonRemoveFriend(ActionEvent actionEvent) {
        Utilizator user1 = tableView.getSelectionModel().getSelectedItem();
        Utilizator user2 = utilizatorService.findd(user1.getFirstName(),user1.getLastName()).get();
        try {
            friendshipsService.deletePrietenie(user.getId(), user2.getId());
            friendRequestsService.deleteRequest1(user.getId(), user2.getId());
        }
        catch (RepoException s){
            AlertBox alertBox = new AlertBox("Error!",s.getMessage());
            alertBox.display();
        }
        handleButtonFriendCheck();
        model.setAll(getRequestsList());
    }

    public void handleReject(ActionEvent actionEvent) {
        Request friendToAdd = tableViewRequests.getSelectionModel().getSelectedItem();
        Long id = utilizatorService.findd(friendToAdd.getFromName(),friendToAdd.getFromLastName()).get().getId();
        try{
            friendRequestsService.respondRequest(user, Status.valueOf("rejected"),id);
            model.setAll(getRequestsList());
        }
        catch(Exception exception){
            AlertBox alertBox = new AlertBox("Error!", exception.getMessage());
            alertBox.display();
        }
    }


    public void handleButtonAccept(ActionEvent actionEvent) {
        Request friendToAdd = tableViewRequests.getSelectionModel().getSelectedItem();
        Long id = utilizatorService.findd(friendToAdd.getFromName(),friendToAdd.getFromLastName()).get().getId();
        try{
            friendRequestsService.respondRequest(user, Status.valueOf("approved"),id);
            model.setAll(getRequestsList());
        }
        catch(Exception exception){
            AlertBox alertBox = new AlertBox("Error!", exception.getMessage());
            alertBox.display();
        }
    }

    public void handleButtonMess(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MessengerView.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        MessengerController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("Messenger");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handleShowRequests(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/RequestsView.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        RequestsController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService, eventService);
        primaryStage.setTitle("Requests");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handlebuttonReports(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Reports.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        ReportsController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handlebuttonReports1(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/ReportsView.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        ReportsMessagesController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handlerNotButton(ActionEvent actionEvent) {
        eventlist.setVisible(true);
        this.notificationsButton.setText("0");

    }

    public void handlerExit(MouseEvent mouseEvent) {
        eventlist.setVisible(false);
    }

    public void handleShowEvents(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Events.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        EventsController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("Events");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handleCreateEvent(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/NewEvent.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        NewEventController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService,friendRequestsService,eventService);
        primaryStage.setTitle("Events");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }
    private void initUserModel() {
        page.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                pageHandler());
        Page<Utilizator> users = utilizatorService.getPagedUsers(page.getCurrentPageIndex());
        modelGrade.setAll(users.getContent().collect(Collectors.toList()));
    }
    public void pageHandler(){
        if(checkfriend.isSelected()) {
            page.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                    pageHandler());
            Page<Utilizator> utilizatorPage =  utilizatorService.getPagedFriendsOfUser(page.getCurrentPageIndex(), user.getId());
            modelGrade.setAll(utilizatorPage.getContent().collect(Collectors.toList()));
            tableView.setItems(modelGrade);
        }
        else {
            initUserModel();
            //nume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
            //prenume.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
            tableView.setItems(modelGrade);
        }
    }
}