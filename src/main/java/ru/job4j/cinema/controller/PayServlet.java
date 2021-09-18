package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.HallService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PayServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOG = LoggerFactory.getLogger(PayServlet.class.getName());

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HallService service = (HallService) req.getServletContext().getAttribute("service");
        Ticket ticket = service.getTicket((Place) req.getSession().getAttribute("place"));
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
        boolean isSave = service.saveTicket(place, account);
        resp.setContentType("application/json");
        OutputStream output = resp.getOutputStream();
        String json;
        if (isSave) {
            json = GSON.toJson(place);
        } else {
            json = GSON.toJson("back");
            service.removeTicket(place);
            req.getSession().removeAttribute("place");
        }
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
