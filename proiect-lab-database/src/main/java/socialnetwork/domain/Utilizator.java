package socialnetwork.domain;

import java.util.ArrayList;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private ArrayList<Utilizator> friends = new ArrayList<Utilizator>();
    private String Password;
    private Long Salt;
    private String Image;
    private String Username;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        ;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<Utilizator> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        String prieteni = "";
        for (Utilizator friend : friends) {
            prieteni+=friend.getFirstName() + " " + friend.getLastName()+" ";
        }
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + prieteni +
                '}';
    }

    public String  getImage() {
        return String.valueOf(Image);
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

    public void addFriends(Utilizator friend) {
        this.friends.add(friend);
    }

    public  void deleteFriend(Utilizator friend){
        friends.remove(friend);
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setSalt(Long salt) {
        Salt = salt;
    }

    public String getPassword() {
        return Password;
    }

    public Long getSalt() {
        return Salt;
    }
}