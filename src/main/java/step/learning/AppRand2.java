package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class AppRand2 {
    public void run() {
        //регистрируем драйвер для бд
        Driver mysqlDriver;
        System.out.println("App 2  works");
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

        //  выполнение команд
        //  создание
        String sql = "CREATE TABLE IF NOT EXISTS randoms2 (" +
                "id BIGINT PRIMARY KEY DEFAULT UUID_SHORT(), " +
                "num INT NOT NULL," +
                "flt FLOAT(5) NOT NULL," +
                "dt VARCHAR(104) NOT NULL,"+
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB DEFAULT CHARSET = UTF8";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);  //  вариант без возврата данных
            System.out.println("Query OK");
        } catch (SQLException e) {
            System.out.println("Error create");
            return;
        }

        //  заполнение
        Random rnd = new Random();
        int rndInt = rnd.nextInt();
        String rndStr = "Str" + rndInt;
        float rndFloat = rnd.nextFloat();

        sql = "INSERT INTO randoms2 VALUES (UUID_SHORT(),?,?,?,?)";

        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            for (int i = 100500; i < 100503; ++i) {

                rndStr = "Prep " + rnd.nextInt();

                prep.setInt(1, i);
                prep.setFloat(2, rndFloat);
                String time = LocalDateTime.now().toString();
                prep.setString(3, time);
                prep.setString(4, rndStr);
                prep.executeUpdate();
            }
            System.out.println("prep ok");

            //  выводим все
            sql = "SELECT * FROM randoms2";

            try (Statement statement = connection.createStatement();) {
                ResultSet res = statement.executeQuery(sql);
                try {
                    while (res.next()) {
                        System.out.printf("%d  %d %f  %s %s %n",
                                res.getLong(1), res.getInt("num"), res.getFloat(3),res.getString(4), res.getString(5));
                    }
                    System.out.println("Select OK");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    System.out.println(sql);
                    return;
                }

                //  закрываем подключение
                try {
                    connection.close();
                    DriverManager.deregisterDriver(mysqlDriver);
                } catch (SQLException e) {
                    System.out.println("Error");
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}