package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public void run()
    {
        //регистрируем драйвер для бд
        Driver mysqlDriver;
        System.out.println("App works");
        try {
            mysqlDriver = new Driver();
        } catch (SQLException e) {
            System.out.println("Error");
            return;
        }

        try {
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException e) {
            System.out.println("Error");
            return;
        }

        //подключение
        String connectionString =
                "jdbc:mysql://localhost:3306/java191?useUnicode=true&characterEncoding=UTF-8" ;
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString,"user191","pass191");
        } catch (SQLException e) {
            System.out.println("Connection Error");
            return;
        }

        //выполнение команд
        String sql = "CREATE TABLE IF NOT EXISTS randoms (" +
                "id BIGINT PRIMARY KEY, " +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB DEFAULT CHARSET = UTF8";

        try
        {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);  //  вариант без возврата данных
            System.out.println("Query OK");
        }catch (SQLException e) {
            System.out.println("Error");
            return;
        }

        try {
            connection.close();  //  закрываем подключение
            DriverManager.deregisterDriver(mysqlDriver);
        } catch (SQLException e) {
            System.out.println("Error");
            return;
        }


    }
}
