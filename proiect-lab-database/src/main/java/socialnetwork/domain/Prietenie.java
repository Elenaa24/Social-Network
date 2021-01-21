package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Prietenie extends Entity<Tuple<Long, Long>> {

    LocalDateTime date;

    public Prietenie(Tuple tuple) {
        super.setId(tuple);
        date = LocalDateTime.now();
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Prietenie{" + super.toString() +
                "date=" + date.format(formatter) +
                '}';
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}