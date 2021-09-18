package ru.job4j.cinema.model;

import java.util.Objects;

public class Place {

    private int row;
    private int cell;
    private boolean isReserved = false;
    private int session;

    public Place(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    public Place(int row, int cell, int session) {
        this.row = row;
        this.cell = cell;
        this.session = session;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Place place = (Place) o;
        return row == place.row
                && cell == place.cell;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, cell);
    }

    @Override
    public String toString() {
        return "Place{"
                + "row=" + row
                + ", cell=" + cell
                + ", isReserved=" + isReserved
                + ", session=" + session
                + '}';
    }
}
