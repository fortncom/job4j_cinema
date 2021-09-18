package ru.job4j.cinema.model;

import java.sql.Timestamp;
import java.util.Objects;

public class HallSession {

    private int id;
    private Timestamp time;
    private int price;

    public HallSession(int id, Timestamp time, int price) {
        this.id = id;
        this.time = time;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallSession session = (HallSession) o;
        return id == session.id
                && price == session.price
                && Objects.equals(time, session.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, price);
    }

    @Override
    public String toString() {
        return "HallSession{"
                + "id=" + id
                + ", time=" + time
                + ", price=" + price
                + '}';
    }
}
