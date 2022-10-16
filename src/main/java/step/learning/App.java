package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Random;

public class App {
    public void run() {
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
                "jdbc:mysql://localhost:3306/java191?useUnicode=true&characterEncoding=UTF-8";
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");
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

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);  //  вариант без возврата данных
            System.out.println("Query OK");
        } catch (SQLException e) {
            System.out.println("Error");
            return;
        }
        Random rnd = new Random();
        int rndInt = rnd.nextInt();
        String rndStr = "Str" + rndInt;

        sql = String.format("INSERT INTO randoms VALUES (UUID_SHORT(),%d, '%s')", rndInt, rndStr);

        try (
                Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(sql);
            System.out.println("Insert OK");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
            return;
        }

        sql = "SELECT * FROM randoms";
        try (Statement statement = connection.createStatement()) {
            ResultSet res = statement.executeQuery(sql);

            while (res.next()) {
                System.out.printf("%d %d %s %n", res.getLong(1),
                        res.getInt(2), res.getString("str"));
            }
            ;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
            return;
        }

        sql = "SELECT r.id, r.num, r.str FROM randoms AS r";
        try (Statement statement = connection.createStatement()) {
            ResultSet res = statement.executeQuery(sql);

            while (res.next()) {
                System.out.printf("%d %d %s %n",
                        res.getLong(1), res.getInt("num"), res.getString(3));
            }
            ;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
            return;
        }

        //

        sql = "INSERT INTO randoms VALUES (UUID_SHORT(),?,?)";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            for (int i = 100500; i < 100503; ++i) {

                rndStr = "Prep " + rnd.nextInt();

                prep.setInt(1, i);
                prep.setString(2, rndStr);
                prep.executeUpdate();
            }
            System.out.println("prep ok");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
            return;
        }
        System.out.println("------------------------------------------------");
        sql = "SELECT * FROM randoms ORDER BY num DESC ";
        try (Statement statement = connection.createStatement();
             ResultSet res = statement.executeQuery(sql)) {
            while (res.next()) {
                System.out.printf("%d  %d  %s %n",
                        res.getLong(1), res.getInt("num"), res.getString(3));
            }
            try {
                connection.close();  //  закрываем подключение
                DriverManager.deregisterDriver(mysqlDriver);
            } catch (SQLException e) {
                System.out.println("Error");
                return;
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
