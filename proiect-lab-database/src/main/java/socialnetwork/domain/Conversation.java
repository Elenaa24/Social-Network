package socialnetwork.domain;

import java.util.List;

public class Conversation {
    String users_string;
    List<Utilizator> users;
    public Conversation(List<Utilizator> users) {
        this.users = users;
        String aux ="";
        for (Utilizator user : users) {
            aux += user.getFirstName()+" "+user.getLastName()+", ";
        }
        aux = aux.substring(0,aux.length()-2);
        this.users_string = aux;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "users_string='" + users_string + '\'' +
                '}';
    }

    public List<Utilizator> getUsers() {
        return users;
    }

    public String getUsers_string() {
        return users_string;
    }
}
