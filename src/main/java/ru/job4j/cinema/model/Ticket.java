package ru.job4j.cinema.model;

import java.util.Objects;

public class Ticket {

    private int id;
    private HallSession sessionId;
    private int row;
    private int cell;
    private Account account;

    public Ticket(int id, int row, int cell, Account account, HallSession sessionId) {
        this.id = id;
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HallSession getSessionId() {
        return sessionId;
    }

    public void setSessionId(HallSession sessionId) {
        this.sessionId = sessionId;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id
                && row == ticket.row
                && cell == ticket.cell
                && Objects.equals(sessionId, ticket.sessionId)
                && Objects.equals(account, ticket.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sessionId, row, cell, account);
    }

    @Override
    public String toString() {
        return "Ticket{"
                + "id=" + id
                + ", sessionId=" + sessionId
                + ", row=" + row
                + ", cell=" + cell
                + ", account=" + account
                + '}';
    }
}
