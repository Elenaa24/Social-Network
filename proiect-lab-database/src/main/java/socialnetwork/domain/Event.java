package socialnetwork.domain;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event extends Entity<Long> {
    String name;
    LocalDateTime date;
    Long organizator;
    List<Tuple<Long,Boolean>> participanti = new ArrayList<>();
    String location;

    public Event(String name, LocalDateTime date, Long organizator,String Location) {
        this.name = name;
        this.date = date;
        this.organizator = organizator;
        this.location = Location;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public List<Tuple<Long, Boolean>> getParticipanti() {
        return participanti;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setOrganizator(Long organizator) {
        this.organizator = organizator;
    }

    public void setParticipanti(List<Tuple<Long,Boolean>> participanti) {
        this.participanti = participanti;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getOrganizator() {
        return organizator;
    }

    public void addParticipant(Long participant, Boolean modify) {
        this.participanti.add(new Tuple<>(participant,modify));
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", organizator=" + organizator +
                ", participanti=" + participanti +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return this.getId()==event.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, organizator);
    }
}
