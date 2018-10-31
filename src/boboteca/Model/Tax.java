package boboteca.Model;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Tax {
    private Integer id;
    private Book book;
    private User user;
    private String description;
    private Double value;

    public Tax(Integer id, Book book, User user, String description, Double value) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.description = description;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
