package step.learning.dao;

import step.learning.entities.MyUser;
import step.learning.entities.User;
import step.learning.services.hash.HashService;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MyUserDAO {
    private final Connection connection ;
    private final HashService hashService ;

    @Inject
    public MyUserDAO(Connection connection, HashService hashService) {
        this.connection = connection ;

        this.hashService = hashService;
    }

    public String hashPassword( String password, String salt ) {
        return hashService.hash( salt + password + salt ) ;
    }

    public String add( MyUser user ) {
        // генерируем id для новой записи
        String id = UUID.randomUUID().toString() ;
        // генерируем соль
        String salt = hashService.hash( UUID.randomUUID().toString() ) ;
        // генерируем хеш пароля
        String passHash = this.hashPassword( user.getPass(), salt ) ;

        user.setSalt(salt);
        user.setPass(passHash);
        System.out.println(user.getSalt());

        String sql = "INSERT INTO MyUsers(`id`,`login`,`pass`,`name`,`salt`) VALUES(?,?,?,?,?)" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, id ) ;
            prep.setString( 2, user.getLogin() ) ;
            prep.setString( 3, user.getPass() ) ;
            prep.setString( 4, user.getName() ) ;
            prep.setString( 5, salt ) ;
            prep.executeUpdate() ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            return null ;
        }
        return id ;
    }

    public boolean isLoginUsed( String login ) {
        String sql = "SELECT COUNT(u.`id`) FROM MyUsers u WHERE u.`login`=?" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, login ) ;
            ResultSet res = prep.executeQuery() ;
            res.next() ;
            return res.getInt(1) > 0 ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
            return true ;
        }


    }

    public MyUser getUserByCredentialsOld(String login, String pass ) {
        String sql = "SELECT u.* FROM MyUsers u WHERE u.`login`=? AND u.`pass`=?" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, login ) ;
            prep.setString( 2, this.hashPassword( pass, "" ) ) ;
            ResultSet res = prep.executeQuery() ;
            if( res.next() ) return new MyUser( res ) ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
        }
        return null ;
    }

    public MyUser getUserByCredentials( String login, String pass ) {
        String sql = "SELECT u.* FROM MyUsers u WHERE u.`login`=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                MyUser user = new MyUser(res);
                //pass - открытый пароль, user.pass - Hash(pass,user.salt)
                String expectedHash = this.hashPassword(pass, user.getSalt());
                System.out.println(user.getSalt());
                System.out.println(user.getPass());
                System.out.println(expectedHash);
                if (expectedHash.equals(user.getPass())) {

                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
