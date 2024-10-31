package project.spring.service.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import project.spring.service.models.Room;
import project.spring.service.models.Tenant;
import project.spring.service.models.Type;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Component
public class HotelRepositoryJdbcTemplateImpl implements CrudRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HotelRepositoryJdbcTemplateImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Optional<Tenant> findTenantById(Long id) {
        String sql = "SELECT * FROM Hotel.tenants WHERE id = ?";
        List<Tenant> tenants = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new Tenant(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone_number"), rs.getInt("room"))
        );
        return tenants.isEmpty() ? Optional.empty() : Optional.of(tenants.getFirst());
    }

    @Override
    public Optional<Type> findTypeById(Long id) {
        String sql = "SELECT * FROM Hotel.types WHERE id = ?";
        List<Type> types = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new Type(rs.getInt("id"), rs.getString("type"))
        );
        return types.isEmpty() ? Optional.empty() : Optional.of(types.getFirst());
    }

    @Override
    public Optional<Room> findRoomById(Long id) {
        String sql = "SELECT * FROM Hotel.rooms WHERE id = ?";
        List<Room> rooms = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new Room(rs.getInt("id"), rs.getString("type"), rs.getInt("price"), rs.getInt("footage"), rs.getBoolean("occupation"), rs.getInt("tenant"))
        );
        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms.getFirst());
    }

    @Override
    public List<Room> findAllRooms() {
        String sql = "SELECT * FROM Hotel.rooms";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Room(rs.getInt("id"), rs.getString("type"), rs.getInt("price"), rs.getInt("footage"), rs.getBoolean("occupation"), rs.getInt("tenant"))
        );
    }

    @Override
    public List<Type> findAllTypes() {
        String sql = "SELECT * FROM Hotel.types";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Type(rs.getInt("id"), rs.getString("type"))
        );
    }

    @Override
    public List<Tenant> findAllTenants() {
        String sql = "SELECT * FROM Hotel.tenants";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Tenant(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("phone_number"), rs.getInt("room"))
        );
    }

    @Override
    public void saveType(Type type) {
        String sql = "INSERT INTO Hotel.types (type) VALUES (?)";
        jdbcTemplate.update(sql, type.getType());
    }

    @Override
    public void saveRoom(Room room) {
        String sql = "INSERT INTO Hotel.rooms (type, price, footage, occupation, tenant) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, room.getType(), room.getPrice(), room.getFootage(), room.isOccupation(), room.getTenantID());
        if (room.getTenantID() != null) {
            if (findTenantById(Long.parseLong(String.valueOf(room.getTenantID()))).isPresent()) {
                jdbcTemplate.update("UPDATE Hotel.tenants SET room= ? WHERE id = ?", findAllRooms().getLast().getId(), room.getTenantID());
            }
        }
    }

    @Override
    public int saveTenant(Tenant tenant) {
        String sql = "INSERT INTO Hotel.tenants (name, surname, phone_number, room) VALUES (?, ?, ?, ?)";
        try {
            if (tenant.getRoom() != null) {
                if (findRoomById(Long.parseLong(String.valueOf(tenant.getRoom()))).isPresent()) {
                    jdbcTemplate.update(sql, tenant.getName(), tenant.getSurname(), tenant.getPhoneNumber(), tenant.getRoom());
                    jdbcTemplate.update("UPDATE Hotel.rooms SET occupation = TRUE, tenant = ? WHERE id = ?", findAllTenants().getLast().getId(), tenant.getRoom());
                    return 0;
                } else {
                    return 1;
                }
            }
            jdbcTemplate.update(sql, tenant.getName(), tenant.getSurname(), tenant.getPhoneNumber(), tenant.getRoom());
        } catch (org.springframework.dao.DuplicateKeyException e) {
            if (e.getMessage().contains("tenants_room_key")) {
                return 2;
            } else {
                return 3;
            }
        }
        return 0;
    }


    @Override
    public void deleteType(Long id) {
        Type type = findTypeById(id).get();
        List<Room> rooms = findAllRooms();
        for (Room room : rooms) {
            if (room.getType().equals(type.getType())) {
                if (room.isOccupation()) {
                    jdbcTemplate.update("UPDATE Hotel.tenants SET room = null WHERE id = ?", room.getTenantID());
                }
                jdbcTemplate.update("DELETE from hotel.rooms WHERE id = ?", room.getId());
            }
        }
        jdbcTemplate.update("DELETE from hotel.types WHERE id = ?", id);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = findRoomById(id).get();
        if (room.isOccupation()) {
            jdbcTemplate.update("UPDATE Hotel.tenants SET room = null WHERE id = ?", room.getTenantID());
        }
        String sql = "DELETE FROM Hotel.rooms WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteTenant(Long id) {
        Tenant tenant = findTenantById(id).get();
        if (tenant.getRoom() != 0) {
            unrentARoom(id);
        }
        String sql = "DELETE FROM Hotel.tenants WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void rentARoom(Long roomID, Long tenantID) {
        String sqlUpdateRoom = "UPDATE Hotel.rooms SET occupation = TRUE, tenant = ? WHERE id = ?";
        jdbcTemplate.update(sqlUpdateRoom, tenantID, roomID);

        String sqlUpdateTenant = "UPDATE Hotel.tenants SET room = ? WHERE id = ?";
        jdbcTemplate.update(sqlUpdateTenant, roomID, tenantID);
    }

    @Override
    public void unrentARoom(Long tenantID) {
        Tenant tenant = findTenantById(tenantID).get();
        String sqlUpdateRoom = "UPDATE Hotel.rooms SET occupation = False, tenant = null WHERE id = ?";
        jdbcTemplate.update(sqlUpdateRoom, tenant.getRoom());

        String sqlUpdateTenant = "UPDATE Hotel.tenants SET room = null WHERE id = ?";
        jdbcTemplate.update(sqlUpdateTenant, tenant.getId());
    }
}
