package socialnetwork.domain;

import socialnetwork.repository.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request extends Entity<Tuple<Long, Long>> {
    Utilizator from;
    Utilizator to;
    Status status;
    private String fromName;
    private String fromLastName;
    private LocalDateTime data;
    private String toName;
    private String toLastName;

    public Request(Utilizator from, Utilizator to, Status status) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.fromName = from.getFirstName();
        this.fromLastName = from.getLastName();
        this.toName = to.getFirstName();
        this.toLastName = to.getLastName();
        this.data = LocalDateTime.now();
    }

    public Request(Status status, String fromName, String fromLastName, LocalDateTime data) {
        this.status = status;
        this.fromName = fromName;
        this.fromLastName = fromLastName;
        this.data = data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getFromName() {
        return fromName;
    }

    public String getFromLastName() {
        return fromLastName;
    }

    public String getData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return data.format(formatter);
    }

    public Utilizator getFrom() {
        return from;
    }

    public Utilizator getTo() {
        return to;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getToName() {
        return toName;
    }

    public String getToLastName() {
        return toLastName;
    }

    @Override
    public String toString() {
        return "Request{" +
                "from=" + from +
                ", to=" + to +
                ", status=" + status +
                '}';
    }

    public void setTo(Utilizator to) {
        this.to = to;
    }
}
