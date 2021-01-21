package socialnetwork.ui;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;
//import socialnetwork.service.ComunitatiService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Scanner;

public class UIadmin {
    UtilizatorService utilizatorService;
    PrietenieService prietenieService;
    //ComunitatiService comunitatiService;

    public UIadmin(UtilizatorService utilizatorService, PrietenieService prietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
//        this.comunitatiService = comunitatiService;
    }

    public void run(){
        Scanner myObj = new Scanner(System.in);
        while(true){
            System.out.println("Introduceti comanda:");
            String comanda = myObj.nextLine();
            String[] comanda1 = comanda.split(" ");
            if(comanda.matches("add user")) {
                System.out.println("ID: ");
                long id=0;
                try {
                    String id1 = myObj.nextLine();
                    id = Long.parseLong(id1);
                }catch (NumberFormatException exception){
                    System.out.println("ID-ul trebuie sa fie un numar!");
                    break;
                }
                System.out.println("Numele: ");
                String nume = myObj.nextLine();
                System.out.println("Prenumele: ");
                String prenume = myObj.nextLine();
                Utilizator u =new Utilizator(nume,prenume);
                u.setId(id);
                try {
                    utilizatorService.addUtilizator(u);
                }catch (RepoException | ValidationException exception){
                    System.out.println(exception.getMessage());
                }
            }
            else{
                if(comanda.matches("delete user [0-9]+")){
                    int id = Integer.parseInt(comanda1[2]);
                    try {
                        utilizatorService.deleteUtilizator((long) id);
                    }catch (RepoException exception){
                        System.out.println(exception.getMessage());
                    }
                }
                else
                if(comanda.matches("add friendship [0-9]+ [0-9]+")) {
                    int id = Integer.parseInt(comanda1[2]);
                    int id2 = Integer.parseInt(comanda1[3]);
                    try {
                        prietenieService.addPrietenie(Long.valueOf(id), Long.valueOf(id2));
                    }catch (RepoException exception){
                        System.out.println(exception.getMessage());
                    }
                }
                else
                if(comanda.matches("unfriend [0-9]+ [0-9]+")){
                    int id = Integer.parseInt(comanda1[1]);
                    int id2 = Integer.parseInt(comanda1[2]);
                    try {
                        prietenieService.deletePrietenie(id,id2);
                    } catch (RepoException  e) {
                        System.out.println(e.getMessage());;
                    }
                }
                else
                if(comanda.matches("exit"))
                    return;

                else
                    System.out.println("Comanda invalida!");
                }
            }
        }
    }

