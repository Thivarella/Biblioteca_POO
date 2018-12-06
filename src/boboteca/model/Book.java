package boboteca.model;

public class Book {
    private Integer id;
    private String name;
    private String author;
    private String category;
    private String year;
    private Generic priority;
    private Boolean disponibility;

    public Book() {}

    public Book(Integer id, String name, String author, String category, String year, Generic priority, Boolean disponibility) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.category = category;
        this.year = year;
        this.priority = priority;
        this.disponibility = disponibility;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Generic getPriority() {
        return priority;
    }

    public void setPriority(Generic priority) {
        this.priority = priority;
    }

    public Boolean getDisponibility() {
        return disponibility;
    }

    public void setDisponibility(Boolean disponibility) {
        this.disponibility = disponibility;
    }

}
