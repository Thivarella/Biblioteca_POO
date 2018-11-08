package boboteca.Model;

public class User {
    private Integer id;
    private String name;
    private String password;
    private Generic gender;
    private Generic category;
    private Address address;
    private String telephone;
    private Boolean isLibrarian;

    public User() {
    }

    public User(Integer id, String name, Generic gender, Generic category, Address address, String telephone, Boolean isLibrarian) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.category = category;
        this.address = address;
        this.telephone = telephone;
        this.isLibrarian = isLibrarian;
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

    public Generic getGender() {
        return gender;
    }

    public void setGender(Generic gender) {
        this.gender = gender;
    }

    public Generic getCategory() {
        return category;
    }

    public void setCategory(Generic category) {
        this.category = category;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getLibrarian() {
        return isLibrarian;
    }

    public void setLibrarian(Boolean librarian) {
        isLibrarian = librarian;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
