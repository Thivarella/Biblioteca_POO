package boboteca.Model;

public class Generic {
    Integer id;
    String label;

    public Generic(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String gender) {
        this.label = label;
    }

    @Override
    public String toString() {
        return  label;
    }
}
