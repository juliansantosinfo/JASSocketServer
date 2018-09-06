/* 
 * Copyright (C) 2018 Julian A. Santos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.juliansantosinfo.jassocketserver.server;

import com.github.juliansantosinfo.jassocketserver.connection.ConnectionManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julian A. Santos
 */
public class ServerManagerConnection implements Runnable {

    private final Server server;
    private Socket connection;
    
    /**
     * Constructor
     * @param server 
     */
    public ServerManagerConnection(Server server) {
        this.server = server;
    }
    
    /**
     * Method to manage new connections.
     */
    public void waitConnection() {

        while (!server.getServerSocket().isClosed()) {

            try {

                // Waiting for client connection.
                connection = server.getServerSocket().accept();

                // Check connection limit.
                if (server.getConnectionList().size() >= server.getConnectionLimit()) {
                    
                    // Send false for client.
                    new DataOutputStream(connection.getOutputStream()).writeBoolean(false);
                    
                    // Terminates requested connection.
                    connection.close();

                    // Write connection rejection in log file.
                    server.addToLog("LIMIT OF CONNECTIONS EXCEEDED.");
                    server.addToLog("REJECTED  CONNECTION OF " + connection.getInetAddress().getHostName());

                } else {

                    // Write a new connection to the log file.
                    server.addToLog("ACCEPT NEW CONNECTION OF " + connection.getInetAddress().getHostName());

                    // ConnectionManager thread instance to manage the connection.
                    ConnectionManager connectionManager = new ConnectionManager(server, connection);
                    connectionManager.start();

                    // Add connection to the list.
                    server.getConnectionList().add(connectionManager);
                    
                    // Send true for client.
                    new DataOutputStream(connection.getOutputStream()).writeBoolean(true);
                }

            } catch (IOException ex) {
                Logger.getLogger(ServerManagerConnection.class.getName()).log(Level.SEVERE, null, ex);
                server.addToLog(ex.getMessage().toUpperCase());
            }
        }

    }
    
    /**
     * Start thread for instance of the class.
     */
    @Override
    public void run() {
        waitConnection();
    }

}
