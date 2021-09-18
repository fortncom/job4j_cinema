package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(HallServlet.class.getName());

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
        HallService service = (HallService) req.getServletContext().getAttribute("service");
        Place checkDoubleReserve = (Place) req.getSession().getAttribute("place");
        if (checkDoubleReserve != null && service.getTicket(checkDoubleReserve) != null) {
            service.removeTicket(checkDoubleReserve);
        }
        String json;
        if (service.addTicket(place)) {
            place.setReserved(true);
            req.getSession().setAttribute("place", place);
            json = GSON.toJson("next");
            LOG.info("Место было забронировано: " + place);
        } else {
            String format = String.format(
                    "Место %s в ряду %s уже было куплено, выберите пожалуйста другое.",
                    place.getCell(), place.getRow());
            json = GSON.toJson(format);
        }
            output.write(json.getBytes(StandardCharsets.UTF_8));
            output.flush();
            output.close();
    }
}
