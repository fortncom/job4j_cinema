package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cinema.model.HallSession;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.service.HallService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HallServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        HallService service = (HallService) req.getServletContext().getAttribute("service");
        resp.setContentType("application/json; charset=utf-8");
        String json = GSON.toJson(service.findAllPlaces());
        OutputStream output = resp.getOutputStream();
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(
            HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Place place = GSON.fromJson(req.getReader(), Place.class);
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        place.setReserved(true);
        req.getSession().setAttribute("place", place);
        HallSession session = ((HallService) req.getServletContext().getAttribute("service"))
                .findSessionById(place.getSession());
        req.getSession().setAttribute("session", session);
        String json = GSON.toJson("next");
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
