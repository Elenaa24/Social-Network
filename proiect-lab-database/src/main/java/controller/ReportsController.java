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
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.service.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.time.LocalDate;

public class ReportsController {
    private Stage primaryStage;
    private UtilizatorService utilizatorService;
    private PrietenieService friendshipsService;
    private MessageService messagesService;
    private RequestService friendRequestsService;
    private EventService eventService;
    Utilizator user;

    @FXML
    DatePicker Start;
    @FXML
    DatePicker End;
    @FXML
    CategoryAxis dateAxis =  new CategoryAxis();
    @FXML
    NumberAxis numberAxis =  new NumberAxis();
    @FXML
    BarChart<String,Number> barChart = new BarChart<String, Number>(dateAxis,numberAxis);

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void setUser(Utilizator user){
        this.user = user;
    }

    public void initialize() {
        //numberAxis.setTickLabelRotation(90);
    }
    public void setServices(UtilizatorService utilizatorService, PrietenieService friendshipsService,
                            MessageService messagesService, RequestService friendRequestsService, EventService eventService){
        this.utilizatorService= utilizatorService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.friendRequestsService = friendRequestsService;
        this.eventService = eventService;
    }

    public void handlerButtonDownload(ActionEvent actionEvent) throws FileNotFoundException, DocumentException {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(user.getLastName()+"_"+user.getFirstName()+"_report"+".pdf"));
        doc.open();
        Paragraph paragraph1 = new Paragraph("Custom report for user activity: " +user.getLastName()+"\n");

        com.itextpdf.text.Font bold = new com.itextpdf.text.Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL);
        Paragraph paragraph = new Paragraph("Messages");
        paragraph.setFont(bold);


        PdfPTable table = new PdfPTable(5);

        Paragraph paragraph2 = new Paragraph("New friendships");

        PdfPTable table2 = new PdfPTable(1);


        Stream.of("ID", "From", "To", "Text", "Date").forEach(table::addCell);

        List<Message> messageList = StreamSupport.stream(messagesService.getMessagesForId1(user).spliterator(), false)
                .collect(Collectors.toList());
        Map<Utilizator, LocalDate> prietenies = friendshipsService.getFriends(user.getId());
        List<Message> messageList1 = new ArrayList<>();
        List<Utilizator> l = new ArrayList<>();
        LocalDate begin = Start.getValue();
        LocalDate end = End.getValue();
        prietenies.forEach((x, y) -> {
            System.out.println(x.getFirstName() + "|" + x.getLastName() + "|" + y);
        });

        for (LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            messageList1.addAll(messageList.stream()
                    .filter(message -> message.getDate().getDayOfMonth() == finalDate.getDayOfMonth() && message.getDate().getMonthValue()==finalDate.getMonthValue())
                    .collect(Collectors.toList()) );

            l.addAll(prietenies.entrySet().stream()
                    .filter(x -> x.getValue().getDayOfMonth() == finalDate.getDayOfMonth() && x.getValue().getMonthValue()==finalDate.getMonthValue())
                    .map(x->x.getKey())
                    .collect(Collectors.toList()));
        }
        messageList1
                .forEach(val -> {
                    table.addCell(val.getId().toString());
                    table.addCell(val.getFrom().getFirstName());
                    table.addCell(val.TOString());
                    table.addCell(val.getMessage());
                    table.addCell(val.getDate().toString());
                });


        paragraph.add(table);
        doc.add(paragraph1);
        doc.add(paragraph);
        String prieteni = ": ";
        String result = "";
        for (Utilizator utilizator : l) {
            prieteni += utilizator.getFirstName()+" "+utilizator.getLastName()+", ";
        }
        if ((prieteni != null) && (prieteni.length() > 0)) {
            result = prieteni.substring(0, prieteni.length()-2);
        }

        paragraph2.add(result);
        doc.add(paragraph2);

        doc.close();

    }

    public void handlerButtonGenerate(ActionEvent actionEvent) {
        barChart.getData().clear();

        try {
            LocalDate begin = Start.getValue();
            LocalDate end = End.getValue();

            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Messages");

            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Friendships");

            Stream<Message> messageList1 = messagesService.getMessagesForId1(user);
            List<Message> messageList = messageList1.collect(Collectors.toList());

            List<Request> friendRequestsList = friendRequestsService.getRequests(user);
            Map<Utilizator, LocalDate> prietenies = friendshipsService.getFriends(user.getId());

            for (LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)) {

                LocalDate finalDate = date;
                long nrMessages = messageList.stream()
                        .filter(message -> message.getDate().getDayOfMonth() == finalDate.getDayOfMonth() && message.getDate().getMonthValue() == finalDate.getMonthValue())
                        .collect(Collectors.toList())
                        .stream().count();

                series1.getData().add(new XYChart.Data(finalDate.toString(), nrMessages));

                int nrFriendRequests = prietenies.entrySet().stream()
                        .filter(x -> x.getValue().getDayOfMonth() == finalDate.getDayOfMonth() && x.getValue().getMonthValue() == finalDate.getMonthValue())
                        .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()))
                        .size();

                series2.getData().add(new XYChart.Data(finalDate.toString(), nrFriendRequests));

            }
            barChart.setBarGap(2);
            barChart.setCategoryGap(20);
            barChart.getData().addAll(series1, series2);
        }
        catch (NullPointerException s){
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
