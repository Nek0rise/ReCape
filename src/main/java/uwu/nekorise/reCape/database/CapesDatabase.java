package uwu.nekorise.reCape.database;

import org.bukkit.plugin.java.JavaPlugin;
import uwu.nekorise.reCape.entity.CapeEntity;

import java.io.File;
import java.sql.*;

public class CapesDatabase {

    private final JavaPlugin plugin;
    private Connection connection;

    public CapesDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "recape.db");
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            createTable();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite Database.", e);
        }
    }

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS player_capes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    cape_type TEXT NOT NULL
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public CapeEntity getById(int id) {
        String sql = "SELECT * FROM player_capes WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new CapeEntity(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("cape_type")
            );

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CapeEntity getByUsername(String username) {
        String sql = "SELECT * FROM player_capes WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new CapeEntity(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("cape_type")
            );

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeById(int id) {
        String sql = "DELETE FROM player_capes WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeByUsername(String username) {
        String sql = "DELETE FROM player_capes WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveOrUpdate(CapeEntity entity) {
        String sql = """
                INSERT INTO player_capes (username, cape_type)
                VALUES (?, ?)
                ON CONFLICT(username)
                DO UPDATE SET cape_type = excluded.cape_type;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getUsername().toLowerCase());
            ps.setString(2, entity.getCapeType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
