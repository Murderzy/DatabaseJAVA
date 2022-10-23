package step.learning.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MyUser {
    private String id;
    private String login;
    private String pass;
    private String name;
    private String salt;

    public MyUser() {

    }
    public MyUser( ResultSet res ) throws SQLException {
        id    = res.getString( "id"    ) ;
        login = res.getString( "login" ) ;
        pass  = res.getString( "pass"  ) ;
        name  = res.getString( "name"  ) ;
        salt = res.getString("salt");
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
