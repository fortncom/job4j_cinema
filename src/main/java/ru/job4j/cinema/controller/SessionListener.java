package ru.job4j.cinema.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Place;
import ru.job4j.cinema.service.HallService;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class.getName());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(900);
        LOG.info("Created new session: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        HallService hallService = (HallService) session.getServletContext().getAttribute("service");
        hallService.removeTicket((Place) session.getAttribute("place"));
        LOG.info("Destroyed session: " + session.getId());
    }
}
