package step.learning;

import step.learning.dao.MyUserDAO;
import step.learning.dao.UserDAO;
import step.learning.entities.MyUser;
import step.learning.entities.User;
import step.learning.services.hash.HashService;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AppMyUser {
    private final Connection connection ;   // injected field

    private final MyUserDAO myUserDAO ;

    @Inject
    public AppMyUser( Connection connection, MyUserDAO myUserDAO ) {
        this.connection = connection ;
        this.myUserDAO = myUserDAO ;
    }

    public void run() {
        String sql = "CREATE TABLE  IF NOT EXISTS  MyUsers (" +
                "    `id`    CHAR(36)     NOT NULL   COMMENT 'UUID'," +
                "    `login` VARCHAR(32)  NOT NULL," +
                "    `pass`  CHAR(40)     NOT NULL   COMMENT 'SHA-160 hash'," +
                "    `name`  TINYTEXT     NOT NULL," +
                "    `salt` CHAR(40)  ,"+
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
            default:
                System.out.println("Incorrect command");
                break;
        }
    }

    private boolean authUser() {
        Scanner kbScanner = new Scanner( System.in ) ;
        String login;
        String pass;
        do {
            System.out.print("Enter login: ");
            login = kbScanner.nextLine();
        }while(login.equals(" ") || login.equals( "" ));
        do {
            System.out.print("Enter password: ");
            pass = kbScanner.nextLine();
        }while(pass.equals(" ") || pass.equals( "" ));
        MyUser user = myUserDAO.getUserByCredentials( login, pass ) ;
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

        while( true ) {
            System.out.print( "Enter login: " ) ;
            login = kbScanner.nextLine();
            if( login.equals( "" ) ) {
                System.out.println( "Login could not be empty" ) ;
                continue ;
            }
            if( myUserDAO.isLoginUsed( login ) ) {
                System.out.println( "Login in use" ) ;
                continue ;
            }
            break ;
        }


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

        System.out.print( "Enter real name: " ) ;
        String name = kbScanner.nextLine() ;
        kbScanner.close() ;

        MyUser user = new MyUser() ;
        user.setLogin( login ) ;
        user.setPass( pass ) ;
        user.setName( name ) ;
        user.setSalt("");
        String id = myUserDAO.add( user ) ;
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
