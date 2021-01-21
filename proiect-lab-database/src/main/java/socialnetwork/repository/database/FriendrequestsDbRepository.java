package socialnetwork.repository.database;

import socialnetwork.domain.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.Status;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendrequestsDbRepository implements Repository<Tuple<Long,Long>, Request> {
    private String url;
    private String username;
    private String password;

    public FriendrequestsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Request> findOne(Tuple<Long, Long> longLongTuple) {
        Connection connection = null;
        Optional<Request> me = Optional.empty();
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement messageStatement = connection.prepareStatement("SELECT * FROM \"FriendRequests\" WHERE (\"From\" = ? AND \"To\" =?) OR (\"From\" = ? AND \"To\" =?);");
            messageStatement.setLong(1,longLongTuple.getRight());
            messageStatement.setLong(2,longLongTuple.getLeft());
            messageStatement.setLong(3,longLongTuple.getLeft());
            messageStatement.setLong(4,longLongTuple.getRight());
            ResultSet resultSet = messageStatement.executeQuery();
            while (resultSet.next()){
            Long id = resultSet.getLong("Id");
            Long from = resultSet.getLong("From");
            Long to = resultSet.getLong("To");
            Timestamp date = resultSet.getTimestamp("Data");
            Status status = Status.valueOf(resultSet.getString("Status"));
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Id\"=?");
            preparedStatement.setLong(1,from);
            ResultSet  resultSet1 = preparedStatement.executeQuery();
            Utilizator user1 = null,user2 = null;
            while(resultSet1.next()) {
                String nume = resultSet1.getString("Nume");
                String prenume = resultSet1.getString("Prenume");
                user1 = new Utilizator(nume,prenume);
                user1.setId(from);
            }
            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Id\"=?");
            preparedStatement2.setLong(1,to);
            ResultSet  resultSet2 = preparedStatement2.executeQuery();
            while(resultSet2.next()) {
                    String nume = resultSet2.getString("Nume");
                    String prenume = resultSet2.getString("Prenume");
                    user2 = new Utilizator(nume,prenume);
                    user2.setId(to);
            }
            Request request = new Request(user1,user2,status);
            request.setData(date.toLocalDateTime());
            request.setId(new Tuple<>(from,to));
            me = Optional.of(request);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return me;
    }

    @Override
    public Iterable<Request> findAll() {
        Connection connection = null;
        List<Request> requests = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement messageStatement = connection.prepareStatement("SELECT * FROM \"FriendRequests\";");
            ResultSet resultSet = messageStatement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                Long from = resultSet.getLong("From");
                Long to = resultSet.getLong("To");
                Timestamp date = resultSet.getTimestamp("Data");
                Status status = Status.valueOf(resultSet.getString("Status"));
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Id\"=?");
                preparedStatement.setLong(1,from);
                ResultSet  resultSet1 = preparedStatement.executeQuery();
                Utilizator user1 = null,user2 = null;
                while(resultSet1.next()) {
                    String nume = resultSet1.getString("Nume");
                    String prenume = resultSet1.getString("Prenume");
                    user1 = new Utilizator(nume,prenume);
                    user1.setId(from);
                }
                PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Id\"=?");
                preparedStatement2.setLong(1,to);
                ResultSet  resultSet2 = preparedStatement2.executeQuery();
                while(resultSet2.next()) {
                    String nume = resultSet2.getString("Nume");
                    String prenume = resultSet2.getString("Prenume");
                    user2 = new Utilizator(nume,prenume);
                    user2.setId(to);
                }
                Request request = new Request(user1,user2,status);
                request.setData(date.toLocalDateTime());
                request.setId(new Tuple<>(from,to));
                requests.add(request);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return requests;
    }


    @Override
    public Optional<Request> save(Request entity) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO \"FriendRequests\"(\"From\",\"To\",\"Status\",\"Data\") VALUES(?,?,?,?);");
            preparedStatement.setLong(1,entity.getFrom().getId());
            preparedStatement.setLong(2,entity.getTo().getId());
            preparedStatement.setString(3, String.valueOf(entity.getStatus()));
            preparedStatement.setTimestamp(4,Timestamp.valueOf(entity.getData()));
            int resultSet = preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Request> delete(Tuple<Long, Long> longLongTuple) {
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement messageStatement = connection.prepareStatement("DELETE FROM \"FriendRequests\" WHERE (\"From\" = ? AND \"To\" =?) OR (\"From\" = ? AND \"To\" =?);");
            messageStatement.setLong(1,longLongTuple.getRight());
            messageStatement.setLong(2,longLongTuple.getLeft());
            messageStatement.setLong(3,longLongTuple.getLeft());
            messageStatement.setLong(4,longLongTuple.getRight());
            messageStatement.executeUpdate();
        } catch (SQLException throwables) {
        throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Request> update(Request entity) {
        try {
            Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"FriendRequests\" SET \"Status\"=? WHERE \"From\"=? AND \"To\"=?;");
            preparedStatement.setLong(2,entity.getFrom().getId());
            preparedStatement.setLong(3,entity.getTo().getId());
            preparedStatement.setString(1, String.valueOf(entity.getStatus()));
            preparedStatement.executeUpdate();
            return Optional.of(entity);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Request> findd(String a, String b) {
        return Optional.empty();
    }

    @Override
    public Optional<Request> finddd(String a) {
        return null;
    }
}
