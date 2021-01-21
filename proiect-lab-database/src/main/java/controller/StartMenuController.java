package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import static java.nio.file.StandardCopyOption.*;

public class StartMenuController {
    @FXML
    TextField txt_username;
    @FXML
    TextField txt_password;

    UtilizatorService utilizatorService;
    MessageService messageService;
    PrietenieService prietenieService;
    RequestService requestService;
    EventService eventService;
    Stage primaryStage;

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = friendshipsService;
        this.messageService = messagesService;
        this.requestService = friendRequestsService;
        this.eventService = eventService;

    }

    public void handleButtonLogInClick(ActionEvent actionEvent) throws NoSuchAlgorithmException, IOException {
        String username = txt_username.getText();
        System.out.println(username);
        String password = txt_password.getText();
        System.out.println(password);
        try {
            Utilizator user1 = utilizatorService.getPass(username);
            if (user1.getPassword().equals(utilizatorService.generateHash(password, "MD5", utilizatorService.longToBytes(user1.getSalt())))) {
                FXMLLoader loader;
                loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/UserMenu.fxml"));
                AnchorPane root = loader.load();
                Scene userScene = new Scene(root, 700, 500);

                UserMenuController userMenuController = loader.getController();
                userMenuController.setPrimaryStage(primaryStage);
                userMenuController.setUser(user1);
                System.out.println(user1);
                userMenuController.setServices(utilizatorService, prietenieService, messageService, requestService, eventService);
                primaryStage.setTitle("User Menu");
                primaryStage.setScene(userScene);
                primaryStage.show();
            } else {
                AlertBox alertBox = new AlertBox("Login failed!", "Username or password is incorrect");
                alertBox.display();
            }
        } catch (RepoException ex) {
            AlertBox alertBox = new AlertBox("Login failed!", "Username or password is incorrect");
            alertBox.display();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void handlerCreate(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/CreateAccount.fxml"));
        AnchorPane root = loader.load();
        Scene userScene = new Scene(root, 400, 300);
        NewAccount userMenuController = loader.getController();
        userMenuController.setPrimaryStage(primaryStage);
        userMenuController.setServices(utilizatorService, prietenieService, messageService, requestService, eventService);
        primaryStage.setTitle("New Account");
        primaryStage.setScene(userScene);
        primaryStage.show();

        }
    }



