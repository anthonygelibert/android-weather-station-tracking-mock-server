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
            try
            {
                new Thread(new ServerThread(serverSocket.accept())).start();
            }
            finally
            {
                serverSocket.close();
            }
        }
        catch (final IOException ignore)
        {
            System.err.println("Can't create the server or accept a new connection.");
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
                    final String response = "[\n{\'id\': \'Montélimar\', \'libellé\': \'Montélimar sud\'},\n{\'id\': \'Chatuzange\', \'libellé\': \'Autoroute Chatuzange\'}\n]"; // NON-NLS
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
