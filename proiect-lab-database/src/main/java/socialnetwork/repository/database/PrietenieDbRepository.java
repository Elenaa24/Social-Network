package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieDbRepository implements Repository<Tuple<Long,Long>, Prietenie>  {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        Optional<Prietenie> o = Optional.empty();
        String query = "SELECT * FROM \"Friendships\" WHERE \"User1\" = ? and \"User2\" = ?;";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1,longLongTuple.getLeft());
            statement.setLong(2,longLongTuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long user1 = resultSet.getLong(2);
                Long user2 = resultSet.getLong(3);
                Date data = resultSet.getDate(4);
                Prietenie prietenie = new Prietenie(new Tuple(user1,user2));
                o=Optional.of(prietenie);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return o;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from \"Friendships\" ");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("Id");
                Long user1 = resultSet.getLong("User1");
                Long user2 = resultSet.getLong("User2");
                Prietenie prietenie = new Prietenie(new Tuple(user1,user2));
                LocalDate date = resultSet.getDate("Date").toLocalDate();
                LocalTime time = resultSet.getTime("Time").toLocalTime();
                prietenie.setDate(date.atTime(time));
                prietenii.add(prietenie);
            }
            return prietenii;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenii;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        Connection connection = null;
        //this.validator.validate(entity);
        Tuple id = entity.getId();
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO public.\"Friendships\"(\"User1\", \"User2\", \"Date\", \"Time\")VALUES (?,?,?,?);");
            statement.setLong(1, (Long) id.getLeft());
            statement.setLong(2, (Long) id.getRight());
            LocalDate a = LocalDate.of(entity.getDate().getYear(),entity.getDate().getMonthValue(),entity.getDate().getDayOfMonth());
            statement.setDate(3, Date.valueOf(a));
            LocalTime b = LocalTime.of(entity.getDate().getHour(),entity.getDate().getMinute(),entity.getDate().getSecond());
            statement.setTime(4, Time.valueOf(b));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> findd(String a, String b) {
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> finddd(String a) {
        return null;
    }

    @Override
public Optional<Prietenie> delete(Tuple<Long, Long> aLong) {
    if (aLong == null)
        throw new IllegalArgumentException("id must not be null");
    Optional<Prietenie> o = findOne(aLong);
    try (Connection connection = DriverManager.getConnection(url, username, password);
         PreparedStatement statement = connection.prepareStatement("DELETE FROM \"Friendships\" WHERE (\"User1\"=? AND \"User2\"=?) OR (\"User2\"=? AND \"User1\"=?) ");
    ) {

        statement.setLong(1, aLong.getLeft());
        statement.setLong(2, aLong.getRight());
        statement.setLong(3, aLong.getLeft());
        statement.setLong(4, aLong.getRight());
        statement.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return o;
}

}
