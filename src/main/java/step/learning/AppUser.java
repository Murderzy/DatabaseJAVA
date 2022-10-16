package step.learning;

import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.hash.HashService;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AppUser {
    private final Connection connection ;   // injected field
    private final HashService hashService ;
    private final UserDAO userDAO ;

    @Inject
    public AppUser( Connection connection, HashService hashService, UserDAO userDAO ) {
        this.connection = connection ;
        this.hashService = hashService ;
        this.userDAO = userDAO ;
    }

    public void run() {
        String sql = "CREATE TABLE  IF NOT EXISTS  Users (" +
                "    `id`    CHAR(36)     NOT NULL   COMMENT 'UUID'," +
                "    `login` VARCHAR(32)  NOT NULL," +
                "    `pass`  CHAR(40)     NOT NULL   COMMENT 'SHA-160 hash'," +
                "    `name`  TINYTEXT     NOT NULL," +
                "    PRIMARY KEY(id)" +
                " ) Engine=InnoDB  DEFAULT CHARSET = UTF8" ;
        try( Statement statement = connection.createStatement() ) {
            statement.executeUpdate( sql ) ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
            return ;
        }
        System.out.print( "1 - Registration\n2 - Log in\nEnter choice: " ) ;
        Scanner kbScanner = new Scanner( System.in ) ;
        int userChoice = kbScanner.nextInt() ;
        switch( userChoice ) {
            case 1: this.regUser() ; break ;
            case 2: this.authUser() ; break ;
        }
    }

    private boolean authUser() {
        Scanner kbScanner = new Scanner( System.in ) ;
        System.out.print( "Enter login: " ) ;
        String login = kbScanner.nextLine() ;
        System.out.print( "Enter password: " ) ;
        String pass = kbScanner.nextLine() ;
        User user = userDAO.getUserByCredentials( login, pass ) ;
        if( user == null ) {
            System.out.println( "ACCESS DENIED" ) ;
            return false ;
        }
        System.out.println( "Hello, " + user.getName() ) ;
        return true ;
    }

    private boolean regUser() {
        Scanner kbScanner = new Scanner( System.in ) ;
        String login ;
        String pass ;
        // region Enter login
        while( true ) {
            System.out.print( "Enter login: " ) ;
            login = kbScanner.nextLine();
            if( login.equals( "" ) ) {
                System.out.println( "Login could not be empty" ) ;
                continue ;
            }
            if( userDAO.isLoginUsed( login ) ) {
                System.out.println( "Login in use" ) ;
                continue ;
            }
            break ;
        }
        // endregion
        // region Enter password
        while( true ) {
            System.out.print( "Enter password: " ) ;
            pass = kbScanner.nextLine() ;
            if( pass.equals( "" ) ) {
                System.out.println( "Password required" ) ;
                continue ;
            }
            System.out.print( "Repeat password: " ) ;
            String pass2 = kbScanner.nextLine() ;
            if( ! pass2.equals( pass ) ) {
                System.out.println( "Passwords mismatch" ) ;
                continue ;
            }
            break ;
        }
        // endregion
        System.out.print( "Enter real name: " ) ;
        String name = kbScanner.nextLine() ;
        kbScanner.close() ;

        User user = new User() ;
        user.setLogin( login ) ;
        user.setPass( pass ) ;
        user.setName( name ) ;
        String id = userDAO.add( user ) ;
        if( id == null ) {
            System.out.println( "Registration error" ) ;
            return false ;
        }
        else {
            System.out.println( "OK, id: " + id ) ;
            return true ;
        }
    }
}