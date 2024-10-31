package project.spring.service.models;

public class Room {
    private Integer id;
    private final String type;
    private final Integer price;
    private final Integer footage;
    private final boolean occupation;
    private Integer tenantID;

    public Room(Integer id, String type, Integer price, Integer footage, boolean occupation, Integer tenantID) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.footage = footage;
        this.occupation = occupation;
        this.tenantID = tenantID;
    }

    public Room(String type, Integer price, Integer footage, boolean occupation) {
        this.type = type;
        this.price = price;
        this.footage = footage;
        this.occupation = occupation;
        this.tenantID = null;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getFootage() {
        return footage;
    }

    public boolean isOccupation() {
        return occupation;
    }

    public Integer getTenantID() {
        return tenantID;
    }

    public void setTenantID(Long id) {
        this.tenantID = Integer.parseInt(String.valueOf(id));
    }

    @Override
    public String toString() {
        return id+ " Номер" +
                ", тип: "  +  type+
                ", цена: " + price +
                ", площадь: " + footage +(occupation?" занят "+tenantID+ " арендатором\n":" свободен\n ");
    }
}
