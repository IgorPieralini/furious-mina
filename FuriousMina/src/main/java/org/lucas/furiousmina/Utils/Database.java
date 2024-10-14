package org.lucas.furiousmina.Utils;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.lucas.furiousmina.model.CustomPlayer;

import java.io.IOException;
import java.sql.*;

public class Database {
    private final String HOST;
    private final int PORT;
    private final String DATABASE;
    private final String USERNAME;
    private final String PASSWORD;
    private Connection connection;

    public Database(String HOST, int PORT, String DATABASE, String USERNAME, String PASSWORD) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.DATABASE = DATABASE;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true",
                USERNAME,
                PASSWORD);

        PreparedStatement ps = connection.prepareStatement("" +
                "CREATE TABLE IF NOT EXISTS MINA(\n" +
                "ID varchar(255),\n" +
                "PLAYER varchar(255),\n" +
                "BLOCOS int\n" +
                ");");

        ps.executeUpdate();
    }

    public void addPlayer(Player player, int blocosQuebrados)throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO MINA (ID,PLAYER,BLOCOS) VALUES (?,?,?);");

        if(getPlayer(player.getUniqueId().toString()) == null){
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setInt(3, blocosQuebrados);
            ps.executeUpdate();
        }
    }

    public void updatePlayer(CustomPlayer player)throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE MINA SET BLOCOS = ? where ID = ?");
        ps.setInt(1, player.getBlocosQuebrados());
        ps.setString(2, player.getId());
        ps.executeUpdate();
    }

    public void removePlayer(String id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM MINA WHERE ID = ?;");
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CustomPlayer getPlayer(String id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM MINA WHERE ID=?;");
        ps.setString(1, id);
        ResultSet resultSet = ps.executeQuery();

        if(!resultSet.next()){
            return null;
        };
        return new CustomPlayer(resultSet.getString("ID"), resultSet.getString("PLAYER"), resultSet.getInt("BLOCOS"));
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void disconnect() {
        try {
            if (isConnected()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

