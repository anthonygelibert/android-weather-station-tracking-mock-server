package org.iutvalence.android.weathertrack.mockserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Create the server.
 *
 * @author Sebastien JEAN, Anthony GELIBERT.
 * @version 1.0.0
 */
public final class ServerMain
{
    /** Default host is all interfaces. */
    private static final String DEFAULT_HOST = "0.0.0.0";
    /** Default port. */
    private static final int    DEFAULT_PORT = 8888;

    public static void main(final String... args)
    {
        // Process params
        final String host;
        final int port;
        switch (args.length)
        {
            // app.jar
            case 0:
                port = DEFAULT_PORT;
                host = DEFAULT_HOST;
                break;
            // app.jar <PORT>
            case 1:
                host = DEFAULT_HOST;
                port = Integer.parseInt(args[0]);
                break;
            // app.jar <@IP> <port>
            case 2:
                host = args[0];
                port = Integer.parseInt(args[1]);
                break;
            default:
                // TODO Display usage.
                return;
        }


        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(host), port));
        }
        catch (final Exception ignore)
        {
            System.err.printf("Could not bind socket on %s/%s...exiting%n", args[0], args[1]);
            return;
        }
        System.out.println("Server started");
        while (true)
        {
            Socket s = null;
            try
            {
                s = serverSocket.accept();
                PrintStream ps = new PrintStream(s.getOutputStream());
                ps.println("HTTP/1.1 200 OK"); // NON-NLS
                final String response = "[\n{\'id\': \'Montélimar\', \'libellé\': \'Montélimar sud\'},\n{\'id\': \'Chatuzange\', \'libellé\': \'Autoroute Chatuzange\'}\n]"; // NON-NLS
                ps.printf("Content-Length: %d%n", response.length()); // NON-NLS
                ps.println();
                ps.println(response);
                //ps.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not accept incoming connection...ignoring");
            }

            System.out.printf("Connection from %s%n", s.getRemoteSocketAddress());
            try
            {
                s.close();
            }
            catch (IOException e)
            {
                // ignoring this
            }
        }
    }
}
