package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, MessageValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Message> findOne(Long aLong) {
        Optional<Message> me = Optional.empty();
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"Messages\" WHERE \"Id\"=?;");
            preparedStatement.setLong(1,aLong);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("Id");
            Long from = resultSet.getLong("From");
            Long reply = resultSet.getLong("Reply");
            String txt = resultSet.getString("Text");
            LocalDate data = resultSet.getDate("Date").toLocalDate();
            LocalTime ora = resultSet.getTime("Time").toLocalTime();
            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Id\"=?;");
            preparedStatement1.setLong(1,from);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            resultSet1.next();
            String nume = resultSet1.getString("Nume");
            String prenume = resultSet1.getString("Prenume");
            Utilizator user = new Utilizator(nume,prenume);
            user.setId(from);
            Message m = new Message(user,txt);
            m.setId(id);
            PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM \"Recievers\" WHERE  \"Id_message\" = ? " );
            statement2.setLong(1,aLong);
            ResultSet resultSet2 = statement2.executeQuery();
            List<Utilizator> TO = new ArrayList<>();
            while (resultSet2.next()){
                Long id_to = resultSet2.getLong("Id_user");
                PreparedStatement statement3 = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Users\".\"Id\"= ?");
                statement3.setLong(1,id_to);
                ResultSet resultSet3 = statement3.executeQuery();
                while (resultSet3.next()){
                    String nume_to = resultSet3.getString("Nume");
                    String prenume_to = resultSet3.getString("Prenume");
                    Utilizator to = new Utilizator(nume_to,prenume_to);
                    to.setId(id_to);
                    TO.add(to);
                }
            }
            m.setTo(TO);
            me = Optional.of(m);
            //return Optional.of(m);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return me;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement( "SELECT \"Users\".\"Id\",\"Nume\",\"Prenume\" FROM \"Users\" INNER JOIN \"Messages\" on \"Users\".\"Id\"=\"From\"\n" +
                     "GROUP BY(\"Users\".\"Id\")");
             ResultSet resultSet = statement.executeQuery()) {
            //pt user
            Utilizator user = null;
            Long id = null;
            while (resultSet.next()) {
                id = resultSet.getLong("Id"); // id ul userului de la care primim mesajul
                String nume = resultSet.getString("Nume");
                String prenume = resultSet.getString("Prenume");
                user = new Utilizator(nume,prenume);
                user.setId(id);

            //pt mesaj
            if(id!=null){
            PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM \"Messages\" WHERE \"Messages\".\"From\"=? ;");
            statement1.setLong(1,id);
            ResultSet resultSet1 = statement1.executeQuery();
            while (resultSet1.next()){
                Long id_mes = resultSet1.getLong(1);
                String mesaj = resultSet1.getString("Text");
                Long reply = resultSet1.getLong("Reply");
                LocalDate date = resultSet1.getDate("Date").toLocalDate();
                LocalTime time = resultSet1.getTime("Time").toLocalTime();
                Message message = new Message(user,mesaj);
                message.setDate(date.atTime(time));
                message.setId(id_mes);
                //message.getReply().setId(reply);
                //pt to
                PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM \"Recievers\" WHERE  \"Id_message\" = ? " );
                statement2.setLong(1,id_mes);
                ResultSet resultSet2 = statement2.executeQuery();
                List<Utilizator> TO = new ArrayList<>();
                while (resultSet2.next()){
                    Long id_to = resultSet2.getLong("Id_user");
                    PreparedStatement statement3 = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Users\".\"Id\"= ?");
                    statement3.setLong(1,id_to);
                    ResultSet resultSet3 = statement3.executeQuery();
                    while (resultSet3.next()){
                        String nume_to = resultSet3.getString("Nume");
                        String prenume_to = resultSet3.getString("Prenume");
                        Utilizator to = new Utilizator(nume_to,prenume_to);
                        to.setId(id_to);
                        TO.add(to);
                    }
                }
                message.setTo(TO);
                messages.add(message);
            }
            }}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement messageStatement = connection.prepareStatement("INSERT INTO \"Messages\" (\"From\", \"Text\", \"Reply\" ,\"Time\", \"Date\") VALUES (? , ? , ?, ?, ?) RETURNING \"Id\";  ");
            messageStatement.setLong(1, entity.getFrom().getId());
            messageStatement.setString(2, entity.getMessage());
            LocalDate a = LocalDate.of(entity.getDate().getYear(),entity.getDate().getMonthValue(),entity.getDate().getDayOfMonth());
            messageStatement.setDate(5, Date.valueOf(a));
            LocalTime b = LocalTime.of(entity.getDate().getHour(),entity.getDate().getMinute(),entity.getDate().getSecond());
            messageStatement.setTime(4, Time.valueOf(b));
            if (entity.getReply() != null) {
                messageStatement.setLong(3, entity.getReply().getId());
            } else {
                messageStatement.setLong(3, -1);
            }
            //messageStatement.execute();
            ResultSet rez = messageStatement.executeQuery();
            rez.next();
            Long id = rez.getLong(1);
            messageStatement.close();
            //PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"Messages\" WHERE (\"From\"=? " AND \"Text\"=?); " );
            //preparedStatement.setLong(1,entity.getFrom().getId());
            //preparedStatement.setString(2,entity.getMessage());
            //esultSet resultSet = preparedStatement.executeQuery();
           // Long id =  resultSet.getLong(1);
            for (Utilizator user : entity.getTo()) {
                PreparedStatement recieveStatement = connection.prepareStatement("INSERT INTO \"Recievers\" (\"Id_message\", \"Id_user\") VALUES (?, ?)");
                recieveStatement.setLong(1, id);
                recieveStatement.setLong(2, user.getId());
                recieveStatement.execute();
                recieveStatement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"Messages\" SET \"Reply\"=? WHERE \"Id\"=?;");
            preparedStatement.setLong(1,entity.getReply().getId());
            preparedStatement.setLong(2,entity.getId());
            return Optional.of(entity);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> findd(String a, String b) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> finddd(String a) {
        return null;
    }
}
