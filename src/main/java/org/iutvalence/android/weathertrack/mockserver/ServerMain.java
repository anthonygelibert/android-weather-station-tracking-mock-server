package org.iutvalence.android.weathertrack.mockserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


/** @author Sebastien JEAN, Anthony GELIBERT. */
public final class ServerMain
{
    public static void main(final String... args)
    {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
        }
        catch (final Exception ignore)
        {
            System.err.println("Could not bind socket on " + args[0] + "/" + args[1] + "...exiting");
            System.exit(1);
        }
        System.out.println("Server started");
        while (true)
        {
            Socket s = null;
            try
            {
                s = serverSocket.accept();
                PrintStream ps = new PrintStream(s.getOutputStream());
                ps.println("HTTP/1.1 200 OK");
                final String response = "[\n{\'id\': \'Montélimar\', \'libellé\': \'Montélimar sud\'},\n{\'id\': \'Chatuzange\', \'libellé\': \'Autoroute Chatuzange\'}\n]";
                ps.println("Content-Length: " + response.length());
                ps.println();
                ps.println(response);
                //ps.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not accept incoming connection...ignoring");
            }

            System.out.println("Connection from " + s.getRemoteSocketAddress());
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
