package socialnetwork.ui;

import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;
//import socialnetwork.service.ComunitatiService;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class UI {
    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    MessageService messageService;
    RequestService requestService;

    public UI(UtilizatorService utilizatorService, PrietenieService prietenieService, MessageService messageService, RequestService requestService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.messageService = messageService;
        this.requestService = requestService;
    }

    public void run() {
        while (true) {
            System.out.println("1.Admin");
            System.out.println("2.User");
            System.out.println("3.Exit");
            Scanner myObj = new Scanner(System.in);
            boolean ok = false;
            String comanda = null;
            while (ok == false)
                try {
                    comanda = myObj.nextLine();
                    if(comanda.equals("exit"))
                        return;
                    int id = Integer.parseInt(comanda);
                    ok = true;

                } catch (NumberFormatException ex) {
                    System.out.println("Invalid!");
                }
            switch (Integer.parseInt(comanda)) {
                case 1:
                    UIadmin uIadmin = new UIadmin(utilizatorService, prietenieService);
                    uIadmin.run();
                    break;
                case 2:
                    ok = true;
                    Tuple<String, String> user = null;
                    Utilizator user1 = null;
//                    while (ok) {
//                        String nume, parola;
//                        System.out.println("Username:");
//                        nume = myObj.nextLine();
//                        System.out.println("Password:");
//                        parola = myObj.nextLine();
//                        try {
//                            user1 = utilizatorService.getPass(nume);
////                            System.out.println(user1.getSalt());
////                            System.out.println(user1.getPassword());
////                            System.out.println(utilizatorService.generateHash(parola,"MD5",user1.getSalt().getBytes()[0]));
//                            if(!user1.getPassword().equals(utilizatorService.generateHash(parola,"MD5",user1.getSalt())))
//                                System.out.println("login failed");
//                            else
//                                ok=false;
//                        }catch (RepoException | NoSuchAlgorithmException ex){
//                            System.out.println(ex.getMessage());
//                        }
//                    }
                    UIuser uIuser = new UIuser(user1,messageService,prietenieService,requestService);
                    uIuser.run();
                    break;
                case 3:
                    break;

            }
        }
    }
}

