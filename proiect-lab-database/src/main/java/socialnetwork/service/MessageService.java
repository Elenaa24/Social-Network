package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageService {
    private Repository<Tuple<Long,Long>, Prietenie> prietenieRepository;
    private Repository<Long, Utilizator> userRepository;
    private Repository<Long,Message> messageRepository;

    public MessageService(Repository<Tuple<Long, Long>, Prietenie> prietenieRepository, Repository<Long, Utilizator> utilizatorRepository,Repository<Long,Message> messageRepository) {
        this.prietenieRepository = prietenieRepository;
        this.userRepository = utilizatorRepository;
        this.messageRepository = messageRepository;
    }
    public Optional<Message> sendMessage(Utilizator u, List<Utilizator> users, String mesaj){
        // verific daca toti userii exista
        if (users.stream().anyMatch(userId -> userRepository.findOne(userId.getId()).isEmpty())){
            throw new RepoException("Utilizatorul nu exita");
        }
        List<Utilizator> all = users.stream()
                .map(idee -> userRepository.findOne(idee.getId()).get())
                .collect(Collectors.toList());
        Message message = new Message(userRepository.findOne(u.getId()).get(), mesaj);
        message.setTo(all);
        message.setId(u.getId());
        message.setDate(LocalDateTime.now());
        return messageRepository.save(message);
    }
    public Stream<Message> getMessagesForId(Utilizator user){
        Iterable<Message> mesaje = messageRepository.findAll();
        List<Message> mesajee = new ArrayList<>();
        mesaje.forEach(mesajee::add);
        if(mesajee.size()==0)
            throw new RepoException("Nu exista mesaje!");
        Stream<Message> messages;
        messages = mesajee.stream().filter(x->{
            if(x.getTo().contains(user) || x.getFrom().getId() == user.getId())
                return true;
            return false;
        });
        return messages;
    }
    public Stream<Message> getMessagesForId1(Utilizator user){
        Iterable<Message> mesaje = messageRepository.findAll();
        List<Message> mesajee = new ArrayList<>();
        mesaje.forEach(mesajee::add);
        if(mesajee.size()==0)
            throw new RepoException("Nu exista mesaje!");
        Stream<Message> messages;
        messages = mesajee.stream().filter(x->{
            if(x.getTo().contains(user))
                return true;
            return false;
        });
        return messages;
    }

    public void replyTo(Utilizator user, Long id, String txt){
        Message test = new Message(user,"abc");
        Message message = messageRepository.findOne(id).get();
        if(message == null)
            throw  new RepoException("Nu exista mesajul!");
        Message message2 = new Message(user,txt);
        message2.setReply(message);
        //System.out.println(message2);
        message2.setDate(LocalDateTime.now());
        List<Utilizator> users = new ArrayList<>();
        for (Utilizator utilizator : message.getTo()) {
            if(utilizator.getId()!=user.getId())
                users.add(utilizator);
        }
        users.add(userRepository.findOne(message.getFrom().getId()).get());
        message2.setTo(users);
        message2.setId(user.getId());
        messageRepository.save(message2);
    }

    public List<Message> findConversation(Long id, Long id2){
        Iterable<Message> all = messageRepository.findAll();
        List<Message> allMessages = new ArrayList<>();
        all.forEach(allMessages::add);
        List<Message> inbox = allMessages.stream()
                .filter(x->(x.getFrom().getId()==id ))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
        return  inbox;
    }

    public List<List<Utilizator>> getConversations(Long id){
        List<List<Utilizator>> users = new ArrayList<>();
        Stream<Message> mesaje = getMessagesForId(userRepository.findOne(id).get());
        mesaje.forEach(x->{
            List<Utilizator> aux = new ArrayList<>();
            aux.add(x.getFrom());
            x.getTo().forEach(y->aux.add(y));
            Boolean ok= true;
            Boolean ok1= true;
            for (List<Utilizator> user : users) {
                if((aux.containsAll(user) && user.containsAll(aux))) {
                    ok1 = false;
                    break;
                }
            }
            if(ok1==true || users.isEmpty())
                users.add(aux);
        });
        return users;
    }


    public boolean comper(Message m, List<Utilizator> utilizators){
        //List<Utilizator> utilizators1 = new ArrayList<>();
        //utilizators1 = utilizators;
        if(utilizators.contains(m.getFrom())){
            for (Utilizator utilizator : m.getTo()) {
                if(!utilizators.contains(utilizator))
                    return false;
            }
            for (Utilizator utilizator : utilizators) {
                if(!(m.getTo().contains(utilizator)))
                    if(!(m.getFrom().getId() == utilizator.getId()))
                        return false;
            }
            return true;
        }
        else
            return false;
    }

    public List<Message> getConversation(List<Utilizator> utilizatori){
        Iterable<Message> all = messageRepository.findAll();
        List<Message> allMessages = new ArrayList<>();
        all.forEach(allMessages::add);
        List<Message> inbox = allMessages.stream()
                .filter(x->comper(x,utilizatori))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
        return  inbox;
    }
}
