import dataAccess.MySqlDataAccess;
import server.Server;

public class Main {

    /**
     * Starts the server on the given port. If port is 0 then a random port is used.
     */
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            var server = new Server().run(port);
//            port = server.port();
            System.out.printf("Server started on port %d%n", port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Pet Server:
                java ServerMain <port> [<dburl> <dbuser> <dbpassword> <dbname>]
                """);
    }
}