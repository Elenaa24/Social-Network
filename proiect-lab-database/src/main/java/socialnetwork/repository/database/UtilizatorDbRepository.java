package socialnetwork.repository.database;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Utilizator> findOne(Long aLong) {
        Optional<Utilizator> o = Optional.empty();
        String query = "SELECT * FROM \"Users\" WHERE \"Id\" = ?;";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String nume = resultSet.getString(2);
                String prenume = resultSet.getString(3);
                String  image = resultSet.getString(7);
                Utilizator user = new Utilizator(nume, prenume);
                user.setId(id);
                user.setImage(image);
                o = Optional.of(user);
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return o;
    }

    @Override
    public Optional<Utilizator> findd(String nume, String prenume) {
        Optional<Utilizator> o = Optional.empty();
        String query = "SELECT * FROM \"Users\" WHERE \"Nume\" = ? AND \"Prenume\" = ?;";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nume);
            statement.setString(2, prenume);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String numee = resultSet.getString(2);
                String prenumee = resultSet.getString(3);
                Utilizator user = new Utilizator(numee, prenumee);
                user.setId(id);
                o = Optional.of(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return o;
    }


    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT \"Users\".\"Id\", \"Nume\", \"Prenume\" FROM public.\"Users\" ");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("Id");
                String firstName = resultSet.getString("Nume");
                String lastName = resultSet.getString("Prenume");
                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                PreparedStatement statement1 = connection.prepareStatement(" SELECT * FROM \"Friendships\" WHERE \"User1\"=? OR \"User2\"=?;");
                statement1.setLong(1,id);
                statement1.setLong(2,id);
                ResultSet resultSet1 = statement1.executeQuery();
                while (resultSet1.next()) {
                Long user2 = resultSet1.getLong("User2");
                Long user3 = resultSet1.getLong("User1");
                    if (user2 != id && user2 != 0) {
                        Optional<Utilizator> amic = this.findOne(user2);
                        Utilizator u = amic.get();
                        utilizator.addFriends(u);
                    }
                    if (user3 != id && user3 != 0) {
                        Optional<Utilizator> amic = this.findOne(user3);
                        Utilizator u = amic.get();
                        utilizator.addFriends(u);
                    }
                }
                users.add(utilizator);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        Connection connection = null;
        //this.validator.validate(entity);
        //long id = entity.getId();
        String nume = entity.getLastName();
        String prenume = entity.getFirstName();
        try {
            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO public.\"Users\"( \"Nume\", \"Prenume\",\"Username\",\"Password\",\"Salt\",\"Image\")VALUES (?,?,?,?,?,?) RETURNING \"Id\"; ");
            //statement.setLong(1, id);
            statement.setString(1, nume);
            statement.setString(2, prenume);
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getPassword());
            statement.setLong(5,entity.getSalt());
            statement.setString(6,entity.getImage());
            ResultSet rez = statement.executeQuery();
            rez.next();
            Long id = rez.getLong(1);
            entity.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must not be null");
        Optional<Utilizator> o = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM \"Users\" WHERE \"Id\"=? ");
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        return Optional.empty();
    }
    @Override
    public Optional<Utilizator> finddd(String username1) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Users\" WHERE \"Username\" =?;");
            statement.setString(1, username1);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id = resultSet.getLong(1);
                String nume = resultSet.getString("Nume");
                String prenume = resultSet.getString("Prenume");
                String pass = resultSet.getString("Password");
                Long salt = resultSet.getLong("Salt");
                String image = resultSet.getString("Image");
                Utilizator user = new Utilizator(nume, prenume);
                user.setPassword(pass);
                user.setSalt(salt);
                user.setId(id);
                user.setImage(image);
                return Optional.of(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }
}

