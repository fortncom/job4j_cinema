package ru.job4j.cinema.persistence;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Ticket;

import java.sql.SQLException;
import java.util.Collection;

public interface Store {

    Collection<Ticket> findAllTickets();

    Collection<Account> findAllAccount();

    Collection<HallSession> findAllHallSession();

    void save(Ticket ticket) throws SQLException;

    HallSession create(HallSession session);

    void update(HallSession session);

    Ticket findTicketById(int id);

    Account findAccountById(int id);

    Account findAccountByEmail(String email);

    HallSession findSessionById(int id);
}
