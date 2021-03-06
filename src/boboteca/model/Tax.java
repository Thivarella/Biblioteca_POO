package boboteca.model;

public class Tax {
    private Integer id;
    private Book book;
    private User user;
    private Loan loan;
    private String description;
    private Double value;
    private Boolean isPaid;

    public Tax(Integer id, Book book, User user, Loan loan,String description, Double value, Boolean isPaid) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loan = loan;
        this.description = description;
        this.value = value;
        this.isPaid = isPaid;
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

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
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

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }
}
