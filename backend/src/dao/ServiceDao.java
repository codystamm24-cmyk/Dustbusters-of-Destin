package dao;

import model.Service;
import config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDao {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDao.class);

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT id, title, description, price, duration_minutes FROM services WHERE active = true";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getInt("duration_minutes")
                );
                services.add(service);
            }
        } catch (SQLException e) {
            logger.error("Error fetching services", e);
            throw new RuntimeException("Database error", e);
        }

        return services;
    }

    public Service getServiceById(long id) {
        String sql = "SELECT id, title, description, price, duration_minutes FROM services WHERE id = ? AND active = true";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Service(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("duration_minutes")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching service with id: " + id, e);
            throw new RuntimeException("Database error", e);
        }

        return null;
    }
}