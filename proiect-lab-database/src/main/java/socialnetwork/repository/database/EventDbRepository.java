package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.Request;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.Status;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class EventDbRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;

    public EventDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    @Override
    public Optional<Event> findOne(Long aLong) {
        Connection connection = null;
        Optional<Event> me = Optional.empty();
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement messageStatement = connection.prepareStatement("SELECT * FROM \"Event\" WHERE \"Id\" = ?;");
            messageStatement.setLong(1,aLong);
            ResultSet resultSet = messageStatement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                String nume = resultSet.getString("Nume");
                Timestamp date = resultSet.getTimestamp("Data");
                Long organizator = resultSet.getLong("Organizator");
                String loc = resultSet.getString("Location");
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"UserEvent\" WHERE \"Id_event\"=?");
                preparedStatement.setLong(1,id);
                ResultSet  resultSet1 = preparedStatement.executeQuery();
                List<Tuple<Long,Boolean>> participanti = new ArrayList<>();
                while(resultSet1.next()) {
                    Long id1 = resultSet1.getLong("Id_user");
                    Boolean m = resultSet1.getBoolean("Notify");
                    participanti.add(new Tuple<>(id1,m));
                }
                Event event = new Event(nume,date.toLocalDateTime(),organizator,loc);
                event.setParticipanti(participanti);
                event.setId(id);
                me = Optional.of(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return me;
    }

    @Override
    public Iterable<Event> findAll() {
        List<Event> events = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT \"Event\".\"Id\", \"Event\".\"Nume\", \"Event\".\"Data\", \"Event\".\"Organizator\", \"UserEvent\".\"Id_user\", \"UserEvent\".\"Notify\", \"Event\",\"Event\".\"Location\" FROM \"Event\" left join \"UserEvent\" on \"Event\".\"Id\" = \"UserEvent\".\"Id_event\" ");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                Timestamp date = resultSet.getTimestamp(3);
                Long hostId = resultSet.getLong(4);
                Long participant = resultSet.getLong(5);
                boolean notify = resultSet.getBoolean(6);
                String loc = resultSet.getString(7);
                Event event = new Event(name,date.toLocalDateTime(),hostId,loc);
                event.setId(id);
                if (!events.contains(event)) {
                    if(participant!=0)
                        event.addParticipant(participant,notify);
                    events.add(event);
                } else
                    for (Iterator<Event> it = events.iterator(); it.hasNext(); ) {
                        Event event1 = it.next();
                        if (event1.equals(event))
                            event1.addParticipant(participant,notify);
                    }
            }
            return events;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return events;
    }

    @Override
    public Optional<Event> save(Event entity){
        Connection connection = null;
        try {
        connection = DriverManager.getConnection(url,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO \"Event\"(\"Nume\",\"Data\",\"Organizator\",\"Location\") VALUES(?,?,?,?) RETURNING \"Id\";");
        preparedStatement.setString(1,entity.getName());
        preparedStatement.setLong(3,entity.getOrganizator());
        preparedStatement.setString(4, String.valueOf(entity.getLocation()));
        preparedStatement.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        Long id = resultSet.getLong(1);
        preparedStatement.close();
        List<Tuple<Long, Boolean>> par = entity.getParticipanti();
        if(par.size()!=0){
            PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO \"UserEvent\" VALUES (?,?,?);");
            par.forEach(x->{
                try {
                    preparedStatement1.setLong(1,x.getLeft());
                    preparedStatement1.setLong(2,id);
                    preparedStatement1.setBoolean(3,x.getRight());
                    int resultSet1 = preparedStatement1.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            preparedStatement1.close();
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
        return Optional.empty();
    }

    @Override
    public Optional<Event> delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<Event> o = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM \"Event\" WHERE \"Id\"=? ");
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public Optional<Event> update(Event entity) {
        delete(entity.getId());
        save(entity);
        return Optional.empty();
    }

    @Override
    public Optional<Event> findd(String a, String b) {
        return Optional.empty();
    }

    @Override
    public Optional<Event> finddd(String a) {
        return Optional.empty();
    }
}
