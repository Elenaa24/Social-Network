package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Conversation;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class MessengerController {
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Conversation useri;
    ObservableList<Message> modelGrade = FXCollections.observableArrayList();
    ObservableList<Conversation> modelGrade1 = FXCollections.observableArrayList();
    Utilizator user;
    @FXML
    TableView<Conversation> conversationsTable;
    @FXML
    TableColumn<Conversation, String> ColumnConv;
    @FXML
    TableView<Message> MessageTable;
    @FXML
    TableColumn<Message,String> FromColumn;
    @FXML
    TableColumn<Message,String> MessageColumn;
    @FXML
    TextField txtReply;
    @FXML
    TextField txtMessage;
    @FXML
    TextField txtTo;
    @FXML
    TableColumn <Message,String> TimeColumn;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void initialize() {
       ColumnConv.setCellValueFactory(new PropertyValueFactory<Conversation,String>("users_string"));
       FromColumn.setCellValueFactory(new PropertyValueFactory<Message,String>("nume"));
       MessageColumn.setCellValueFactory(new PropertyValueFactory<Message,String>("message"));
       TimeColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("date_format"));
       conversationsTable.setItems(modelGrade1);
       MessageTable.setItems(modelGrade);
    }

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService){
        this.utilizatorService= utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
        //modelGrade.setAll(getUsersList());
        modelGrade1.setAll(getConversations());
    }
    public List<Message> getMessageList(List<Utilizator> useri){
        return messagesService.getConversation(useri);
    }

    public List<Conversation> getConversations(){
        List<List<Utilizator>> conversations = messagesService.getConversations(user.getId());
        List<Conversation> conversations1 = new ArrayList<>();
        conversations.forEach(x->conversations1.add(new Conversation(x)));
        return conversations1;
    }

    public void handlerSelect(MouseEvent mouseEvent) {
        Conversation usesi = conversationsTable.getSelectionModel().getSelectedItem();
        this.useri = usesi;
        modelGrade.setAll(getMessageList(usesi.getUsers()));
    }

    public void handleButtonReply(ActionEvent actionEvent) {
        //Message m = MessageTable.getSelectionModel().getSelectedItem();
        messagesService.sendMessage(user,useri.getUsers(),txtReply.getText());
        //messagesService.replyTo(user,m.getId(),txtReply.getText());
        modelGrade.setAll(getMessageList(useri.getUsers()));
    }

    public void handleButtonSend(ActionEvent actionEvent) {
        String aux = txtTo.getText();
        String mesaj = txtMessage.getText();
        List<Utilizator> utilizators = new ArrayList<>();
        String [] aux1 = aux.split(" ");
        boolean ok = true;
        for(int i=0;i< aux1.length;i=i+2){
            String nume = aux1[i];
            String prenume = aux1[i+1];
            Optional<Utilizator> userr = utilizatorService.findd(nume, prenume);
            if(userr.isEmpty()){
                ok=false;
                AlertBox alertBox = new AlertBox("Error!", "Nu exista!");
                alertBox.display();
            }
            else{
                Utilizator userrr = userr.get();
                utilizators.add(userrr);
            }
        }
        if(ok==true)
            messagesService.sendMessage(user,utilizators,mesaj);
        utilizators.add(user);
        modelGrade.setAll(getMessageList(utilizators));
        modelGrade1.setAll(getConversations());

    }

    public void handleBack(ActionEvent actionEvent) throws IOException {
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
