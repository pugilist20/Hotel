package project.spring.service.models;

public class Tenant {
    private Integer id;
    private final String name;
    private final String surname;
    private final String phoneNumber;
    private Integer room;

    public Tenant(Integer id, String name, String surname, String phoneNumber, Integer room) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.room = room;
    }

    public Tenant(String name, String surname, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.room = null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return id+" Арендатор" +
                ", имя: " + name +
                ", фамилия: " + surname +
                ", номер телефона: " + phoneNumber +(room!=0?" номер: "+room+"\n":" не заселен\n");
    }
}
