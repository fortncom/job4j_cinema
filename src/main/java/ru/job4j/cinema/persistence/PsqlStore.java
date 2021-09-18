package ru.job4j.cinema.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Ticket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("db.cinema.properties"))) {
            cfg.load(reader);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                 "SELECT t.id tId, t.row tRow, t.cell tCell, "
                         + "t.account_id tAcc, a.id aId, a.username aName, a.email aEmail,"
                         + " a.phone aPhone, t.session_id tSession, s.id sId, s.time sTime, "
                         + "s.price sPrice FROM ticket t left join account a on t.account_id=a.id "
                         + "left join session s on t.session_id=s.id;")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    Ticket ticket = new Ticket(it.getInt("tId"),
                            it.getInt("tRow"), it.getInt("tCell"), null, null);
                    Account account = new Account(it.getInt("aId"), it.getString("aName"),
                            it.getString("aEmail"), it.getString("aPhone"));
                    ticket.setAccount(account);
                    HallSession session = new HallSession(
                            it.getInt("sId"), it.getTimestamp("sTime"),
                            it.getInt("sPrice"));
                    ticket.setSessionId(session);
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return tickets;
    }

    @Override
    public Collection<Account> findAllAccount() {
        List<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    accounts.add(new Account(it.getInt("id"), it.getString("username"),
                            it.getString("email"), it.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return accounts;
    }

    @Override
    public Collection<HallSession> findAllHallSession() {
        List<HallSession> sessions = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM session")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    sessions.add(new HallSession(it.getInt("id"),
                            it.getTimestamp("time"), it.getInt("price")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return sessions;
    }

    @Override
    public void save(Ticket ticket) throws SQLException {
        if (ticket.getId() == 0) {
            create(ticket);
        } else {
            update(ticket);
        }
    }

    private Ticket create(Ticket ticket) throws SQLException {
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement psTicket =  cn.prepareStatement(
                    "INSERT INTO ticket(session_id, row, cell, account_id) "
                            + "VALUES (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ) {
                cn.setAutoCommit(false);
                Account acc = save(ticket.getAccount(), cn);
                psTicket.setInt(1, ticket.getSessionId().getId());
                psTicket.setInt(2, ticket.getRow());
                psTicket.setInt(3, ticket.getCell());
                psTicket.setInt(4, acc.getId());
                psTicket.execute();
                try (ResultSet id = psTicket.getGeneratedKeys()) {
                    if (id.next()) {
                        ticket.setId(id.getInt(1));
                    }
                }

                cn.commit();
            } catch (SQLException e) {
                LOG.error("Exception", e);
                cn.rollback();
            }
        }
        return ticket;
    }

    private void update(Ticket ticket) throws SQLException {
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement ps =  cn.prepareStatement(
                    "update ticket set session_id=?, row=?, cell=?, account_id=? where id=?;")) {
                cn.setAutoCommit(false);
                Account acc = save(ticket.getAccount(), cn);
                ps.setInt(1, ticket.getSessionId().getId());
                ps.setInt(2, acc.getId());
                ps.setInt(3, ticket.getRow());
                ps.setInt(4, ticket.getCell());
                ps.executeUpdate();
                try (ResultSet id = ps.getGeneratedKeys()) {
                    if (id.next()) {
                        ticket.setId(id.getInt(1));
                    }
                }
                cn.commit();
            } catch (SQLException e) {
                LOG.error("Exception", e);
                cn.rollback();
            }
        }
    }

    private Account save(Account account, Connection cn) throws SQLException {
        Account rsl;
        if (account.getId() == 0) {
            rsl = create(account, cn);
        } else {
            rsl = update(account, cn);
        }
        return rsl;
    }

    private Account create(Account account, Connection cn) throws SQLException {
        try (PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO account(username, email, phone) "
                             + "VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        }
        return account;
    }

    private Account update(Account account, Connection cn) throws SQLException {
        try (PreparedStatement ps =  cn.prepareStatement(
                     "update account set username=?, email=?, phone=? where id=?;")
        ) {
            ps.setString(1, account.getName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setInt(4, account.getId());
            ps.executeUpdate();
        }
        return account;
    }

    public HallSession create(HallSession session) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO session(time, price) "
                             + "VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, session.getTime());
            ps.setInt(2, session.getPrice());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    session.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return session;
    }

    public void update(HallSession session) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "update session set time=?, price=? where id=?;")
        ) {
            ps.setTimestamp(1, session.getTime());
            ps.setInt(2, session.getPrice());
            ps.setInt(3, session.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public Ticket findTicketById(int id) {
        Ticket ticket = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT t.id tId,  t.row tRow, t.cell tCell, a.id aId, "
                             + "a.username aName, a.email aEmail, a.phone aPhone, "
                             + "s.id sId, s.time sTime, s.Price sPrice  "
                             + "FROM ticket t left join account a on t.account_id=a.id "
                             + "left join session s on t.id=s.id where t.id=?;")) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    ticket = new Ticket(it.getInt("tId"),  it.getInt("tRow"),
                            it.getInt("tCell"), null, null);
                    Account account = new Account(it.getInt("aId"), it.getString("aName"),
                            it.getString("aEmail"), it.getString("aPhone"));
                    ticket.setAccount(account);
                    HallSession hallSession = new HallSession(
                            it.getInt("sId"), it.getTimestamp("sTime"),
                            it.getInt("sPrice"));
                    ticket.setSessionId(hallSession);
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception", e);
        }
        return ticket;
    }

    @Override
    public Account findAccountById(int id) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account a where a.id=?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    account = new Account(it.getInt("id"), it.getString("username"),
                            it.getString("email"), it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return account;
    }

    @Override
    public Account findAccountByEmail(String email) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account a where a.email=?")
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    account = new Account(it.getInt("id"), it.getString("username"),
                            it.getString("email"), it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return account;
    }

    @Override
    public HallSession findSessionById(int id) {
        HallSession session = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM session s where s.id=?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    session = new HallSession(it.getInt("id"),
                            it.getTimestamp("time"), it.getInt("price"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return session;
    }
}
