package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.persistence.PsqlStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HallService {

    public List<Place> findAllPlaces() {
        List<Place> places = new ArrayList<>();
        List<Integer> hall = PsqlStore.instOf().findHallById(1);
        for (int i = 0; i < hall.size(); i++) {
            for (int j = 0; j < hall.get(i); j++) {
                Place place = new Place(i + 1, j + 1);
                places.add(place);
            }
        }
        Collection<Ticket> tickets = PsqlStore.instOf().findAllTickets();
        for (Ticket ticket : tickets) {
            Place place = new Place(ticket.getRow(), ticket.getCell());
            place.setReserved(true);
            places.set(places.indexOf(place), place);
        }
        places.sort(Comparator.comparingInt(Place::getRow).thenComparing(Place::getCell));
        return places;
    }

    public void saveTicket(Place place, Account account) throws SQLException {
        PsqlStore.instOf().save(new Ticket(
                PsqlStore.instOf().findSessionById(place.getSession()),
                place.getRow(), place.getCell(), account));
    }

    public HallSession findSessionById(int id) {
        return PsqlStore.instOf().findSessionById(id);
    }
}
