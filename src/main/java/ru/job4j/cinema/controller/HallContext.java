package ru.job4j.cinema.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.service.HallService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class HallContext implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(HallContext.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        HallService service = new HallService();
        service.init();
        ServletContext context = event.getServletContext();
        context.setAttribute("service", service);
        LOG.info("Init context");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
