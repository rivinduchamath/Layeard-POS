package lk.ijse.dep.pos.db;

import javafx.scene.control.Alert;
import lk.ijse.dep.DEPCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    public static String username;
    public static String db;
    public static  String password;
    public static String host;
    public static String port;


    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties properties = new Properties();
            File file = new File("resources/application.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();

            String ip=properties.getProperty("pos.ip");
            host=ip;
            String port=properties.getProperty("pos.port");
            this.port = port;

            String user= DEPCrypt.decode(properties.getProperty("pos.user"),"123");
            username=user;
            String password=DEPCrypt.decode(properties.getProperty("pos.password"),"123");
            this.password=password;
            String db=properties.getProperty("pos.db");
            this.db=db;
            connection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+db+"?createDatabaseIfNotExist=true&allowMultiQueries=true", user, password);
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
            ResultSet resultSet = pstm.executeQuery();
            if (!resultSet.next()){
                File dbScriptFile = new File("ss.sql");
                if(!dbScriptFile.exists()){
                    new Alert(Alert.AlertType.INFORMATION,"Cannot Find backup File");
                    throw  new RuntimeException("Unale to find file");
                }
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dbScriptFile)));
                bufferedReader.lines().forEach(s -> sb.append(s));
                bufferedReader.close();
                System.out.println(sb.toString());
                String sql = "";
                pstm = connection.prepareStatement(sb.toString());
                pstm.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance() {
        return (dbConnection == null) ? (dbConnection = new DBConnection()) : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }

}
