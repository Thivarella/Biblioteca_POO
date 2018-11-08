package boboteca.Model;

public class Generic {
    Integer id;
    String label;
    Integer value;
    Double additionalValue;

    public Generic() {
    }

    public Generic(Integer id, String label) {
        this.id = id;
        this.label = label;
        this.value = 0;
        this.additionalValue = 0D;
    }

    public Generic(Integer id, String label, Integer value, Double additionalValue) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.additionalValue = additionalValue;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Double getAdditionalValue() {
        return additionalValue;
    }

    public void setAdditionalValue(Double additionalValue) {
        this.additionalValue = additionalValue;
    }

    @Override
    public String toString() {
        return label;
    }
}
