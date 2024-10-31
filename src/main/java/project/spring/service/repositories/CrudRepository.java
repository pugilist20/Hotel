package project.spring.service.repositories;

import project.spring.service.models.Room;
import project.spring.service.models.Tenant;
import project.spring.service.models.Type;

import java.util.List;
import java.util.Optional;

public interface CrudRepository {
    Optional<Tenant> findTenantById(Long id);
    Optional<Type> findTypeById(Long id);
    Optional<Room> findRoomById(Long id);
    List<Room> findAllRooms();
    List<Type> findAllTypes();
    List<Tenant> findAllTenants();
    void saveType(Type type);
    void saveRoom(Room room);
    int saveTenant(Tenant tenant);
    void deleteTenant(Long id);
    void deleteType(Long id);
    void deleteRoom(Long id);
    void rentARoom(Long roomID, Long tenantID);
    void unrentARoom(Long tenantID);

}
