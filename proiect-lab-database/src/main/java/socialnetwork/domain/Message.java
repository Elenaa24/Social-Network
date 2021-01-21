package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {
    Utilizator from;
    List<Utilizator> to = new ArrayList<>();
    String message;
    LocalDateTime date;
    Message reply = null;
    String nume;
    String date_format;

    public Message(Utilizator from, String message) {
        this.from = from;
        this.message = message;
        this.reply = null;
        //to = new ArrayList<>();
        this.nume = from.getLastName();
    }

    public String getNume() {
        return nume;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        date_format = date.format(myFormat);
    }

    public String getDate_format() {
        return date_format;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public String TOString(){
        String catre="";
        for (Utilizator x : to) {
            catre+=x.getFirstName();
            catre+=" ";
            catre+=x.getLastName();
            catre+="|";
        }
        return catre;
    }
    @Override
    public String toString() {
        String catre="";
        for (Utilizator x : to) {
           catre+=x.getFirstName();
           catre+=" ";
           catre+=x.getLastName();
            catre+="|";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Message{" +
                "from=" + from.getLastName() +" "+ from.getFirstName() +
                ", to=" + catre +
                ", message='" + message + '\'' +
                ", date=" + date.format(formatter) +
                ", reply=" + reply +
                '}';
    }
}
