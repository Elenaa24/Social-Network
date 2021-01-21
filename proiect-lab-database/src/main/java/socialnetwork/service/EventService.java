package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.Request;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.exceptions.RepoException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventService {
    private Repository<Long, Event> eventRepository;

    public EventService(Repository<Long, Event> eventRepository) {
        this.eventRepository = eventRepository;
    }
    public List<Event> getAll(){
        Iterable<Event> all = eventRepository.findAll();
        List<Event> All = new ArrayList<>();
        all.forEach(All::add);
        return All;
    }

    public List<String> getNotifications(Utilizator user){
        Iterable<Event> events = eventRepository.findAll();
        List<String> not = new ArrayList<>();
        events.forEach(x->{
            if(x.getParticipanti().contains(new Tuple<>(user.getId(),true)) || x.getOrganizator() == user.getId()) {
                long diff = ChronoUnit.DAYS.between(LocalDateTime.now(),x.getDate());
                if(diff>0)
                    not.add("Evenimentul: " + x.getName() + " va avea loc in " +diff+" zile.");
            }
        });
        return not;
    }

    public Event findOne(Long id) {
        Optional<Event> a = eventRepository.findOne(id);
        if(a.isEmpty())
            throw new RepoException("Nu exista evenimentul!");
        Event b = a.get();
        return b;
    }

    public void updateEvent(Event event) {
        eventRepository.update(event);
    }

    public void save(Event newEvent) {
        eventRepository.save(newEvent);
    }
}
