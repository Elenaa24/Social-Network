package socialnetwork.ui;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Status;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.RequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIuser {
    Utilizator user;
    MessageService messageService;
    PrietenieService prietenieService;
    RequestService requestService;

    public UIuser(Utilizator user, MessageService messageService, PrietenieService prietenieService, RequestService requestService) {
        this.user = user;
        this.messageService = messageService;
        this.prietenieService = prietenieService;
        this.requestService = requestService;
    }

    public void run() {
        Scanner myObj = new Scanner(System.in);
        String comanda;

        while (true) {
            System.out.println("Introduceti comanda: ");
            comanda = myObj.nextLine();
            String[] comanda1 = comanda.split(" ");
            if (comanda.matches("send to")) {
                System.out.println("Introduceti id-urile:");
                String iduri = myObj.nextLine();
                String[] separat = iduri.split(" ");
                List<Long> iddd = new ArrayList<>();
                for (String s : separat) {
                    Long idd = Long.valueOf(Integer.parseInt(s));
                    iddd.add(idd);
                }
                System.out.println("Introduceti mesajul: ");
                String mesaj = myObj.nextLine();
                //messageService.sendMessage(user, iddd, mesaj);
            } else {
                if (comanda.matches("friends")) {
                    Map<Utilizator, LocalDate> prieteni = prietenieService.getFriends(user.getId());
                    prieteni.forEach((x, y) -> {
                        System.out.println(x.getFirstName() + "|" + x.getLastName() + "|" + y);
                    });
                } else {
                    if (comanda.matches("friends [0-9]+ [A-Z]+")) {
                        String month = comanda1[2];
                        int year = Integer.parseInt(comanda1[1]);
                        Map<Utilizator, LocalDate> prieteni = prietenieService.getFriends1(user.getId(), year, month);
                        prieteni.forEach((x, y) -> {
                            System.out.println(x.getFirstName() + "|" + x.getLastName() + "|" + y);
                        });
                    }
                    if (comanda.matches("reply to")) {
                        Stream<Message> mesaje = messageService.getMessagesForId(user);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        mesaje.forEach(x -> {
                            System.out.println(x.getId() + "." + x.getFrom() + " : " + x.getMessage() + " | " + x.getDate().format(formatter));
                        });
                        System.out.println("Alegeti id-ul mesajului la care vreti sa raspundeti: ");
                        String id_mesaj = myObj.nextLine();
                        Long id_m = Long.valueOf(Integer.parseInt(id_mesaj));
                        System.out.println("Introduceti mesajul: ");
                        String mesaj = myObj.nextLine();
                        messageService.replyTo(user, id_m, mesaj);
                    }
                    if (comanda.matches("exit"))
                        return;
                    if (comanda.matches("show conversation with [0-9]+")) {
                        Long id = Long.valueOf(Integer.parseInt(comanda1[3]));
                        //messageService.findConversation(user.getId(), id).forEach(x-> System.out.println(x.getFrom()+": "+x.getMessage()));
                    }
                    if(comanda.matches("send request to [0-9]+")){
                        Long id = Long.valueOf(Integer.parseInt(comanda1[3]));
                        try {
                            requestService.sendRequest(user, id);
                        }catch (RepoException ex){
                                System.out.println(ex.getMessage());
                        }
                    }
                    if(comanda.matches("show requests")){
                        requestService.getRequests(user).forEach(System.out::println);
                    }
                    if(comanda.matches("accept [0-9]+")){
                        Long id = Long.valueOf(Integer.parseInt(comanda1[1]));
                        try {
                            requestService.respondRequest(user, Status.valueOf("approved"), id);
                        }catch (RepoException ex){
                            System.out.println(ex.getMessage());
                        }
                    }
                    if(comanda.matches("reject [0-9]+")){
                        Long id = Long.valueOf(Integer.parseInt(comanda1[1]));
                        requestService.respondRequest(user, Status.valueOf("rejected"),id);
                    }
                }
            }
        }
    }
}

