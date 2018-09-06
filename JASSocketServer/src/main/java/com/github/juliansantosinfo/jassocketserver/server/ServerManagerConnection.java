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
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Julian A. Santos
 */
public class ServerManagerConnection implements Runnable {

    private final Server server;
    private Socket connection;

    public ServerManagerConnection(Server server) {
        this.server = server;
    }

    public void waitConnection() {

        while (!server.getServerSocket().isClosed()) {

            try {

                // Aceita conexao do cliente.
                connection = server.getServerSocket().accept();

                // Registra log da conexao no servidor.
                server.addToLog("ACCEPT NOVA CONEXAO DE " + connection.getInetAddress().getHostName());

                // Cria thread para gerenciar conexao.
                ConnectionManager connectionManager = new ConnectionManager(server, connection);
                connectionManager.start();

                // Add connection a lista.
                server.getConnectionList().add(connectionManager);

            } catch (IOException ex) {
                server.addToLog(ex.getMessage().toUpperCase());
            }
        }

    }

    @Override
    public void run() {
        waitConnection();
    }

}
