package org.iutvalence.android.weathertrack.mockserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Create the server.
 *
 * @author Sebastien JEAN, Anthony GELIBERT.
 * @version 2.0.0
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
                usage();
                return;
        }

        try
        {
            final ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
            System.out.printf("MockServer listen on %s:%d%n%n", host, port);
            try
            {
                while (true)
                {
                    System.out.println("Wait for new connections…");
                    new Thread(new ServerThread(serverSocket.accept())).start();
                }
            }
            finally
            {
                serverSocket.close();
            }
        }
        catch (final IOException ignore)
        {
            System.err.printf("Can't create the server (%s:%d) or accept a new connection.", host, port);
        }
    }

    /** Print usage. */
    private static void usage()
    { /* TODO */ }

    /**
     * Thread sending JSON data to the client.
     *
     * @author Anthony GELIBERT
     * @version 1.0.0
     */
    private static final class ServerThread implements Runnable
    {
        /** Client Socket. */
        private final Socket m_socket;

        ServerThread(final Socket socket)
        {
            System.out.printf("Request from %s%n", socket.getRemoteSocketAddress());
            m_socket = socket;
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("ServerThread");
            sb.append("{m_socket=").append(m_socket);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public void run()
        {
            try
            {
                final PrintStream ps = new PrintStream(m_socket.getOutputStream());
                try
                {
                    ps.println("HTTP/1.1 200 OK"); // NON-NLS
                    ps.println("Server: AWTSMockServer/2.0.0"); // NON-NLS
                    ps.println("Content-Type: application/json"); // NON-NLS
                    final String response = "[{\'id\':\'Montélimar\',\'libellé\':\'Montélimar sud\'},{\'id\':\'Chatuzange\',\'libellé\':\'Autoroute Chatuzange\'}]"; // NON-NLS
                    ps.printf("Content-Length: %d%n", response.length()); // NON-NLS
                    ps.println();
                    ps.println(response);
                }
                finally
                {
                    ps.close();
                }
            }
            catch (final IOException ignore)
            {
                System.err.println("Can't communicate with the client.");
            }
        }
    }
}
