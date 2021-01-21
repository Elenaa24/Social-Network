package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.Status;
import socialnetwork.repository.exceptions.RepoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestService {
    private Repository<Tuple<Long,Long>, Prietenie> prietenieRepository;
    private Repository<Long, Utilizator> utilizatorRepository;
    private Repository<Tuple<Long,Long>, Request> requestRepository;

    public RequestService(Repository<Tuple<Long, Long>, Prietenie> prietenieRepository, Repository<Long, Utilizator> utilizatorRepository, Repository<Tuple<Long, Long>, Request> requestRepository) {
        this.prietenieRepository = prietenieRepository;
        this.utilizatorRepository = utilizatorRepository;
        this.requestRepository = requestRepository;
    }

    public void sendRequest(Utilizator user, Long id){
        Tuple a = new Tuple(user.getId(),id);
        Optional<Utilizator> utilizator = utilizatorRepository.findOne(id);
        if(utilizator.isEmpty())
            throw new RepoException("Nu exista user-ul!");
        Optional<Prietenie> x = prietenieRepository.findOne(new Tuple<>(user.getId(), id));
        Optional<Prietenie> y = prietenieRepository.findOne(new Tuple<>( id,user.getId()));
        if(!x.isEmpty() || !y.isEmpty())
            throw new RepoException("Deja sunteti prieteni!");

        Optional b = requestRepository.findOne(a);
        if(!b.isEmpty())
            throw new RepoException("Cererea deja exista");
        Status c = Status.valueOf("pending");
        Request request = new Request(user,utilizatorRepository.findOne(id).get(),Status.valueOf("pending"));
        requestRepository.save(request);
        //Prietenie prietenie = new Prietenie(a);
        //prietenieRepository.save(prietenie);
    }

    public List<Request> getRequests(Utilizator user){
        Iterable<Request> all = requestRepository.findAll();
        List<Request> All = new ArrayList<>();
        all.forEach(All::add);
        List<Request> cereri = new ArrayList<>();
//        List<Request> cereri = All.stream()
//                .filter(x->(x.getTo().getId()==user.getId()))
//                .collect(Collectors.toList());
        for (Request request : All) {
            if(request.getTo().getId()==user.getId())
                cereri.add(request);
        }
        return  cereri;
    }

    public void respondRequest(Utilizator user, Status a, Long id){
        Optional<Request> ms = requestRepository.findOne(new Tuple<>(user.getId(),id));
        if(ms.isEmpty())
            throw new RepoException("Nu exista cererea!");
        Request m = requestRepository.findOne(new Tuple<>(user.getId(),id)).get();
        Status c = Status.valueOf("pending");
        if(m.getStatus()!=c)
            throw new RepoException("Cerea deja are un raspuns!");
        m.setStatus(a);
        requestRepository.update(m);
        Prietenie prietenie = new Prietenie(new Tuple(user.getId(),id));
        Status b = Status.valueOf("approved");
        if(a == b)
            prietenieRepository.save(prietenie);
    }

    public void deleteRequest(Long id, Long id1) {
        Status c = Status.valueOf("pending");
        if(requestRepository.findOne(new Tuple<>(id,id1)).get().getStatus() != c)
            throw new RepoException("You have already received an answer to the request!");
        requestRepository.delete(new Tuple<>(id,id1));
    }
    public void deleteRequest1(Long id, Long id1) {
        requestRepository.delete(new Tuple<>(id,id1));
    }
    public List<Request> getMyRequests(Utilizator user){
        Iterable<Request> all = requestRepository.findAll();
        List<Request> All = new ArrayList<>();
        all.forEach(All::add);
        List<Request> cereri = new ArrayList<>();
//        List<Request> cereri = All.stream()
//                .filter(x->(x.getTo().getId()==user.getId()))
//                .collect(Collectors.toList());
        for (Request request : All) {
            if(request.getFrom().getId()==user.getId())
                cereri.add(request);
        }
        return  cereri;
    }
}
