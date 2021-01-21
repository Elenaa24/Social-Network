package controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.Request;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.util.stream.StreamSupport;

public class ReportsMessagesController {
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Utilizator user;
    ObservableList<Utilizator> modelGrade = FXCollections.observableArrayList();

    @FXML
    DatePicker Begin;
    @FXML
    DatePicker End;
    @FXML
    CategoryAxis dateAxis =  new CategoryAxis();
    @FXML
    NumberAxis numberAxis =  new NumberAxis();
    @FXML
    BarChart<String,Number> barChart = new BarChart<String, Number>(dateAxis,numberAxis);
    @FXML
    TableView<Utilizator> usersTable;
    @FXML
    TableColumn<Utilizator,String> name;
    @FXML
    TableColumn<Utilizator,String> last_name;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        last_name.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        usersTable.setItems(modelGrade);
    }
    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService){
        this.utilizatorService= utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
        modelGrade.setAll(getUsers());
    }

    public void handleDownload(ActionEvent actionEvent) throws DocumentException, FileNotFoundException {
        Utilizator user1 = usersTable.getSelectionModel().getSelectedItem();
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(user.getLastName()+"_"+user.getFirstName()+".pdf"));
        doc.open();
        Paragraph paragraph1 = new Paragraph("Custom report for user activity: " +user.getLastName());
        paragraph1.setAlignment(20);
        com.itextpdf.text.Font bold = new com.itextpdf.text.Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
        Paragraph paragraph = new Paragraph("Messages");


        PdfPTable table = new PdfPTable(5);


        Stream.of("ID", "From", "To", "Text", "Date").forEach(table::addCell);

        List<Message> messageList = messagesService.findConversation(user1.getId(),user.getId());
        List<Message> messageList1 = new ArrayList<>();
        LocalDate begin = Begin.getValue();
        LocalDate end = End.getValue();

        for (LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            messageList1.addAll(messageList.stream()
                    .filter(message -> message.getDate().getDayOfMonth() == finalDate.getDayOfMonth() && message.getDate().getMonthValue()==finalDate.getMonthValue())
                    .collect(Collectors.toList()) );

        }
        messageList1
                .forEach(val -> {
                    table.addCell(val.getId().toString());
                    table.addCell(val.getFrom().getFirstName());
                    table.addCell(val.TOString());
                    table.addCell(val.getMessage());
                    table.addCell(val.getDate().toString());
                });

        doc.add(paragraph1);
        paragraph.add(table);
        doc.add(paragraph);
        doc.close();
    }

    public List<Utilizator> getUsers(){
        List<Utilizator> all = utilizatorService.getAllUsers();
        all.remove(user);
        return all;
    }
    public void handleGenerate(ActionEvent actionEvent) {

        barChart.getData().clear();
        Utilizator user1 = usersTable.getSelectionModel().getSelectedItem();
    try{
        LocalDate begin = Begin.getValue();
        LocalDate end = End.getValue();
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Messages");

        List<Message> messageList = messagesService.findConversation(user1.getId(), user.getId());

        for (LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)) {

            LocalDate finalDate = date;
            long nrMessages = messageList.stream()
                    .filter(message -> message.getDate().getDayOfMonth() == finalDate.getDayOfMonth() && message.getDate().getMonthValue() == finalDate.getMonthValue())
                    .collect(Collectors.toList())
                    .stream().count();

            series1.getData().add(new XYChart.Data(finalDate.toString(), nrMessages));

        }
        barChart.setBarGap(2);
        barChart.setCategoryGap(20);
        barChart.getData().addAll(series1);

        }catch (NullPointerException s){
            AlertBox alertBox = new AlertBox("Error!", "Invalid value for date");
            alertBox.display();
            }

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
        userMenuController.setServices(utilizatorService, friendshipsService, messagesService, friendRequestsService, eventService);
        primaryStage.setTitle("User Menu");
        primaryStage.setScene(userScene);
        primaryStage.show();
    }
}
