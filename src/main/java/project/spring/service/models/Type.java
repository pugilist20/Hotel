package project.spring.service.models;

public class Type {
    private int id;
    private String type;

    public Type(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return id +" Тип " +
                ", название: " + type + "\n";
    }
}
