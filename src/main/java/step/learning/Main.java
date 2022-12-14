package step.learning;

import com.google.inject.Guice;

public class Main {
    public static void main(String[] args) {
        //Guice.createInjector(new ConfigModule()).getInstance(App.class).run();
        //Guice.createInjector(new ConfigModule()).getInstance(AppRand2.class).run();

        try( ConfigModule configModule = new ConfigModule() ) {
            Guice
                    .createInjector( configModule )
                    .getInstance( AppMyUser.class )
                    .run() ;
        }
        catch( Exception ex ) {
            System.out.println( "Program terminated: " + ex.getMessage() ) ;
        }
    }
}
