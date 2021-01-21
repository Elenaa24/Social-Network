package socialnetwork.service;

import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.repository.paging.*;


import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService {
    private PagingRepository<Long, Utilizator> repo;

    public UtilizatorService(PagingRepository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    public Optional<Utilizator> addUtilizator(Utilizator utilizator) {
        return repo.save(utilizator);
    }

    public Optional<Utilizator> deleteUtilizator (Long id){
        return repo.delete(id);
    }

    public List<Utilizator> getAllUsers() {
        Iterable<Utilizator> students = repo.findAll();
        return StreamSupport.stream(students.spliterator(), false).collect(Collectors.toList());
    }


    public List<Utilizator> filterUsersName(String s) {
        Iterable<Utilizator> students = repo.findAll();

        List<Utilizator> filteredUsers = StreamSupport.stream(students.spliterator(), false)
                .filter(user -> user.getFirstName().contains(s)).collect(Collectors.toList());


//        Set<Utilizator> filteredUsers1= new HashSet<>();
//        students.forEach(filteredUsers1::add);
//        filteredUsers1.removeIf(student -> !student.getFirstName().contains(s));

        return filteredUsers;
    }

    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    public Optional<Utilizator> findd(String nume, String prenume){
        Optional<Utilizator> user = repo.findd(nume, prenume);
        return  user;
    }

    public  byte[] longToBytes(Long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public  Long createSalt(){
        byte[] bytes = new byte[10];
        SecureRandom random = new SecureRandom();
        Long salt = random.nextLong();
        //System.out.println(salt);
        return salt;
    }
    public  String generateHash(String pass, String alg, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(alg);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(pass.getBytes());
        StringBuilder parola = new StringBuilder();
        for (byte b : hash) {
            parola.append(String.format("%02x", b));
        }
        return parola.toString();
    }


    public Utilizator getPass(String username)  {
        Optional<Utilizator> user = repo.finddd(username);
        if(user.isEmpty())
            throw new RepoException("Username-ul nu exista!");
        Utilizator utilizator = user.get();
        return utilizator;
    }

    public Optional<Utilizator> findOne(Long userr) {
        Optional<Utilizator> user = repo.findOne(userr);
        return  user;
    }

    public Page<Utilizator> getPagedUsers(int page) {
        Pageable p = new PageableImplementation(page,11);
        return repo.findAll(p);
    }

    public Page<Utilizator> getPagedFriendsOfUser(int currentPageIndex, Long id) {
        Pageable p = new PageableImplementation(currentPageIndex,11);
        return repo.findAllFiltered(p,id);
    }
}
