package model;

import java.math.BigDecimal;

public class Service {
    private final long id;
    private final String title;
    private final String description;
    private final BigDecimal price;
    private final int durationMinutes;

    public Service(long id, String title, String description, BigDecimal price, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getDurationMinutes() { return durationMinutes; }
}