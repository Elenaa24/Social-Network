package socialnetwork;

import controller.StartMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.*;
import socialnetwork.ui.UI;

import java.io.IOException;

public class MainFX extends Application {

    private UtilizatorService utilizatorService;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //System.out.println("Reading data from database");
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
        final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");
        PagingRepository<Long, Utilizator> userFileRepository3 =
                new UtilizatorDbPageRepo(url,username, pasword,  new UtilizatorValidator());
        Repository<Tuple<Long,Long>, Prietenie> userFileRepository4 =
                new PrietenieDbRepository(url,username, pasword,  new PrietenieValidator());

        //userFileRepository3.findAll().forEach(x-> System.out.println(x));
        //userFileRepository4.findAll().forEach(x-> System.out.println(x));
        UtilizatorService utilizatorService = new UtilizatorService(userFileRepository3);

        PrietenieService prietenieService = new PrietenieService(userFileRepository3,userFileRepository4);

        Repository<Long, Message> messageRepository = new MessageDbRepository(url,username,pasword,new MessageValidator());
        MessageService messageService = new MessageService(userFileRepository4,userFileRepository3,messageRepository);
        Repository<Tuple<Long,Long>, Request> requestRepository = new FriendrequestsDbRepository(url,username,pasword);
        RequestService requestService = new RequestService(userFileRepository4,userFileRepository3,requestRepository);
        //messageRepository.findAll().forEach(System.out::println);
        Utilizator userr = new Utilizator("Corbos","Raluc");
        userr.setId((long) 1);
        requestService.getRequests(userr);
        //requestRepository.findAll().forEach(System.out::println);
        Repository<Long,Event> eventDbRepository = new EventDbRepository(url,username,pasword);
        //eventDbRepository.findAll().forEach(System.out::println);
        EventService eventService = new EventService(eventDbRepository);
        //UI ui = new UI(utilizatorService,prietenieService,messageService,requestService);
        //messageService.getConversations((long) 3).forEach(System.out::println);
        //eventService.getAll().forEach(System.out::println);
        //System.out.println(eventDbRepository.findOne((long) 1).get());
        //eventDbRepository.save(eventDbRepository.findOne((long) 1).get());
        //eventDbRepository.delete((long) 9);
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/StartMenuView.fxml"));
        AnchorPane root=loader.load();
        StartMenuController ctrl=loader.getController();
        ctrl.setServices(utilizatorService,prietenieService,messageService,requestService,eventService);
        ctrl.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setTitle("Social Network");
        primaryStage.show();
    }
}