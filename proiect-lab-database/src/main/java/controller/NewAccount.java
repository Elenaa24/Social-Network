package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class NewAccount {
    public TextField secondname;
    public TextField firstname;
    public TextField username;
    public PasswordField password;
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    private String img = "C:\\Users\\Elena\\Desktop\\proiect_final\\socialnetwork\\proiect-lab-database\\src\\main\\resources\\Images\\rata.jpg";

    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService) {
        this.utilizatorService = utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
    }
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void handlerPhoto(ActionEvent actionEvent) throws IOException {
        String nume = secondname.getText();
        String prenume = firstname.getText();
        if(nume.equals("") && prenume.equals("")){
            AlertBox alertBox = new AlertBox("Error!","Complete all the fields!");
            alertBox.display();
        }
        else{
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "JPG & PNG Images", "jpg", "png");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getAbsolutePath());
                Path source = Path.of(chooser.getSelectedFile().getAbsolutePath()); //original file
                Path targetDir = Paths.get("C:\\Users\\Elena\\Desktop\\proiect_final\\socialnetwork\\proiect-lab-database\\src\\main\\resources\\Images");
                File f = new File("C:\\Users\\Elena\\Desktop\\proiect_final\\socialnetwork\\proiect-lab-database\\src\\main\\resources\\Images\\" + nume + "_" + prenume +"." +"png");
                f.getParentFile().mkdirs();
                f.createNewFile();
                Files.copy(source, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                img = f.getPath();
             }
        }
    }


    public void handlerCreate(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException {
        String  prenume = firstname.getText();
        String nume = secondname.getText();
        String usernamee = username.getText();
        String pass = password.getText();
        String alg = "MD5";
        Long salt = utilizatorService.createSalt();
        byte[] saalt = utilizatorService.longToBytes(salt);
        String passwordd = utilizatorService.generateHash(pass,alg,saalt);
        if(prenume.equals("") && nume.equals("") && usernamee.equals("") && pass.equals("")){
            AlertBox alertBox = new AlertBox("Error!","Complete all the fields!");
            alertBox.display();
        }
        else{
            Utilizator user = new Utilizator(prenume,nume);
            user.setSalt(salt);
            user.setPassword(passwordd);
            user.setImage(img);
            user.setUsername(usernamee);
            utilizatorService.addUtilizator(user);
            FXMLLoader loader;
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/UserMenu.fxml"));
            AnchorPane root = loader.load();
            Scene userScene = new Scene(root, 700, 500);

            UserMenuController userMenuController = loader.getController();
            userMenuController.setPrimaryStage(primaryStage);
            userMenuController.setUser(user);
            System.out.println(user);
            userMenuController.setServices(utilizatorService, friendshipsService,messagesService,friendRequestsService, eventService);
            primaryStage.setTitle("User Menu");
            primaryStage.setScene(userScene);
            primaryStage.show();
        }
    }
}
