package socialnetwork.domain;

public class EventDTO {
    String organizator;
    String participanti;
    String status;
    String data;
    String nume;
    Long id;

    public EventDTO(String organizator, String participanti, String status, String data,String nume,Long id) {
        this.organizator = organizator;
        this.participanti = participanti;
        this.status = status;
        this.data = data;
        this.nume = nume;
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public String getOrganizator() {
        return organizator;
    }

    public String getParticipanti() {
        return participanti;
    }

    public String getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "organizator='" + organizator + '\'' +
                ", participanti='" + participanti + '\'' +
                ", status='" + status + '\'' +
                ", data='" + data + '\'' +
                ", nume='" + nume + '\'' +
                ", id=" + id +
                '}';
    }
}
