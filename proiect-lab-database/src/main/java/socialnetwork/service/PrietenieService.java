package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PrietenieService {
    private Repository<Tuple<Long,Long>, Prietenie> prietenieRepository;
    private Repository<Long, Utilizator> utilizatorRepository;

    public PrietenieService( Repository<Long, Utilizator> utilizatorRepository,Repository<Tuple<Long, Long>, Prietenie> prietenieRepository) {
        this.prietenieRepository = prietenieRepository;
        this.utilizatorRepository=utilizatorRepository;
    }

    /**
     * @return Returneaza toate prieteniile
     */
    public Iterable<Prietenie> getAll(){
        return prietenieRepository.findAll();
    }

    /**
     * @param id1   Tuple <id1, id2> reprezinta id-ul prieteniei;
     * @param id2
     */
    public void addPrietenie(long id1, long id2)  {

        Optional<Utilizator> utilizator1= utilizatorRepository.findOne(id1);
        if(utilizator1.equals(Optional.empty()))
            throw new RepoException ("nu exista utilizatorul");
        Optional<Utilizator> utilizator2= utilizatorRepository.findOne(id2);
        if(utilizator2.equals(Optional.empty()))
            throw new RepoException ("nu exista utilizatorul");
        Tuple e =new Tuple(id1,id2);
        Prietenie  friendship = new Prietenie(e);
        Optional p = prietenieRepository.findOne(new Tuple(id1,id2));
        Optional p2 = prietenieRepository.findOne(new Tuple(id2,id1));
        if(p.equals(Optional.empty()) && p2.equals(Optional.empty())){
            prietenieRepository.save(friendship);
        }
        else
            throw new RepoException("already exists");
    }

    /**
     *
     * @param id1 Tuple <id1,id2> reprezinta id-ul prieteniei;
     * @param id2
     * @throws FileNotFoundException
     */
    public void deletePrietenie(long id1, long id2)  {
        Optional p = prietenieRepository.findOne(new Tuple(id1,id2));
        Optional p2 = prietenieRepository.findOne(new Tuple(id2,id1));
        if(p.isEmpty() && p2.isEmpty())
            throw  new RepoException("Nu sunteti prieteni!");
        if(!(p.equals(Optional.empty()))){
            prietenieRepository.delete(new Tuple(id1, id2));
        }
        if(!p2.equals(Optional.empty())){
            prietenieRepository.delete(new Tuple<>(id2,id1));
        }

    }

    /**
     *
     * @param id - id-ul utilizatorului caruia trebuie sa ii aflam prietenii
     * @return - returneaza o lista de utilizatori
     */
    public Map<Utilizator, LocalDate> getFriends(long id){
        Iterable<Prietenie> useri = prietenieRepository.findAll();
        List<Prietenie> rez = new ArrayList<Prietenie>();
        useri.forEach(rez::add);
        Map<Utilizator, LocalDate> friends = rez.stream()
                .filter(x->(x.getId().getRight()==id|| x.getId().getLeft()==id))
                .map(x->{if(x.getId().getLeft()==id) return new Tuple<Long, LocalDate>(x.getId().getRight(),x.getDate().toLocalDate());
                else if(x.getId().getRight()==id) return new Tuple<Long, LocalDate>(x.getId().getLeft(), x.getDate().toLocalDate());
                    return null;
                })
                .collect(Collectors.toMap(x->utilizatorRepository.findOne((x.getLeft())).get(), x->x.getRight()));
        return friends;
    }

    public Map<Utilizator,LocalDate> getFriends1(long id, int year, String month){
        Map<Utilizator, LocalDate> rez = getFriends(id);
        Map<Utilizator, LocalDate> friends = rez.entrySet().stream()
                .filter(x->{
                    if(x.getValue().getYear()==year && x.getValue().getMonth().toString().equals(month))
                        return true;
                    return false;
                })
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
        return friends;
    }
}
