package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.persistence.PsqlStore;
import ru.job4j.cinema.service.HallService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class PayServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Place place = (Place) req.getSession().getAttribute("place");
        HallSession session = (HallSession) req.getSession().getAttribute("session");
        Ticket ticket = new Ticket(session, place.getRow(), place.getCell(), null);
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(ticket);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Account account = GSON.fromJson(req.getReader(), Account.class);
        HallService service = (HallService) req.getServletContext().getAttribute("service");
        Place place = (Place) req.getSession().getAttribute("place");
        resp.setContentType("application/json");
        OutputStream output = resp.getOutputStream();
        String json;
        try {
            Account accEmail = PsqlStore.instOf().findAccountByEmail(account.getEmail());
            Account accPhone = PsqlStore.instOf().findAccountByPhone(account.getPhone());
            if (accEmail != null) {
                account = accEmail;
            } else if (accPhone != null) {
                account = accPhone;
            }
            service.saveTicket(place, account);
            json = GSON.toJson(place);
        } catch (SQLException e) {
            e.printStackTrace();
            json = GSON.toJson("409");
        }
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
