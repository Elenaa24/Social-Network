package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorDbPageRepo extends UtilizatorDbRepository implements PagingRepository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbPageRepo(String url, String username, String password, Validator<Utilizator> validator) {
        super(url,username,password,validator);
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        List<Utilizator> list = StreamSupport.stream(findAll().spliterator(), false)
                .collect(Collectors.toList());
        Paginator<Utilizator> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }

    private Optional<Prietenie> findOneFriendship(Tuple<Long, Long> aLong) {
        Optional<Prietenie> o = Optional.empty();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Friendships\" WHERE (\"User1\" = ? and \"User2\" = ?);");
             statement.setLong(1,aLong.getLeft());
             statement.setLong(2,aLong.getRight());
             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("User1");
                Long id2 = resultSet.getLong("User2");
                Tuple<Long, Long> id = new Tuple(id1, id2);
                if (id.equals(aLong)) {
                    Prietenie p = new Prietenie(id);
                    o = Optional.of(p);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }
    @Override
    public Page<Utilizator> findAllFiltered(Pageable pageable, Long user) {
        List<Utilizator> list = StreamSupport.stream(findAll().spliterator(), false)
                .filter(u ->
                        u.getId() != user && (findOneFriendship(new Tuple<Long, Long>(user, u.getId())).isPresent() || findOneFriendship(new Tuple<Long, Long>(u.getId(),user)).isPresent()) )
                .collect(Collectors.toList());
        Paginator<Utilizator> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }
    
}