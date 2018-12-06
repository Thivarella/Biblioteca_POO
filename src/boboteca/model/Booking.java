package boboteca.model;

import java.sql.Date;

public class Booking {
    private Integer id;
    private Book book;
    private User user;
    private Date bookingDate;
    private Boolean staus;

    public Booking(Integer id, Book book, User user, Date bookingDate, Boolean staus) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.bookingDate = bookingDate;
        this.staus = staus;
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

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Boolean getStaus() {
        return staus;
    }

    public void setStaus(Boolean staus) {
        this.staus = staus;
    }
}
