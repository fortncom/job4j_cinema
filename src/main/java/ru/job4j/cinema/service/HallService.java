package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.persistence.PsqlStore;
import ru.job4j.cinema.persistence.Store;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HallService {

    private final ConcurrentHashMap<Place, Ticket> tickets = new ConcurrentHashMap<>();
    private final List<Place> places = new ArrayList<>();
    private final Store store = PsqlStore.instOf();

    public void init() {
        int[][] arrPlaces = {{1, 2, 3, 4, 5}, {1, 2, 3, 4}, {1, 2, 3}, {1, 2}};
        for (int i = 0; i < arrPlaces.length; i++) {
            for (int j = 0; j < arrPlaces[i].length; j++) {
                Place place = new Place(i + 1, arrPlaces[i][j]);
                places.add(place);
            }
        }
        Collection<Ticket> allTickets = store.findAllTickets();
        for (Ticket ticket : allTickets) {
            Place place = new Place(ticket.getRow(), ticket.getCell());
            place.setReserved(true);
            places.set(places.indexOf(place), place);
            tickets.put(place, ticket);
        }
        places.sort(Comparator.comparingInt(Place::getRow).thenComparing(Place::getCell));
        for (Ticket value : tickets.values()) {
            System.out.println(value);
        }
    }

    public Place getPlace(Place place) {
        return places.get(places.indexOf(place));
    }

    public Ticket getTicket(Place place) {
       return tickets.get(place);
    }

    public List<Place> findAllPlaces() {
        return new ArrayList<>(places);
    }

    public boolean saveTicket(Place place, Account account) {
        boolean rsl = true;
        Ticket ticket = tickets.get(place);
        Account acc = store.findAccountByEmail(account.getEmail());
        if (acc == null) {
            acc = account;
        }
        ticket.setAccount(acc);
        try {
            store.save(ticket);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public boolean addTicket(Place place) {
        boolean rsl = false;
        place.setReserved(true);
        HallSession session = store.findSessionById(place.getSession());
        if (session == null) {
            session = store.create(new HallSession(0,
                    Timestamp.valueOf(LocalDateTime.of(
                            2023, 5, 15, 17, 30)), 500));
        }
        if (!places.contains(place)) {
            return rsl;
        }
        Ticket ticket = new Ticket(0, place.getRow(), place.getCell(), null, session);
        rsl = tickets.putIfAbsent(place, ticket) == null;
        if (rsl) {
            places.set(places.indexOf(place), place);
        }
        return rsl;
    }

    public boolean removeTicket(Place place) {
        boolean rsl = false;
        if (tickets.get(place).getAccount() == null) {
            rsl = tickets.remove(place) != null;
            if (rsl) {
                places.get(places.indexOf(place)).setReserved(false);
            }
        }
        return rsl;
    }
}
