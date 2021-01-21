package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Conversation;
import socialnetwork.domain.Request;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class RequestsController {
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Conversation useri;
    ObservableList<Request> modelGrade = FXCollections.observableArrayList();
    Utilizator user;
    @FXML
    public TableColumn<Request,String> FirstNameColumn;
    @FXML
    public TableColumn<Request,String> LastNameColumn;
    @FXML
    public TableColumn<Request,String> StatusColumn;
    @FXML
    public TableColumn<Request,LocalDateTime> DataColumn;
    @FXML
    public TableView<Request> TableRequests;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void initialize() {
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("toName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("toLastName"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<Request,String>("status"));
        DataColumn.setCellValueFactory(new PropertyValueFactory<Request,LocalDateTime>("data"));
        TableRequests.setItems(modelGrade);
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService){
        this.utilizatorService= utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
        modelGrade.setAll(getRequests());
    }

    private List<Request> getRequests() {
        return friendRequestsService.getMyRequests(user);
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/UserMenu.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 700, 500);
        UserMenuController userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setUser(user);
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService, friendRequestsService, eventService);
        primaryStage.setTitle("User Menu");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }

    public void handleDeleteRequest(ActionEvent actionEvent) {
        Request request = TableRequests.getSelectionModel().getSelectedItem();
        try{
        friendRequestsService.deleteRequest(user.getId(),request.getTo().getId());
        modelGrade.setAll(getRequests());
        }catch (RepoException ex){
            AlertBox alertBox = new AlertBox("Error!", ex.getMessage());
            alertBox.display();
        }
    }
}
