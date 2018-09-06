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

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

/**
 *
 * @author Julian A. Santos
 */
public class ServerConfig {

    private final String pathIniFile = com.github.juliansantosinfo.jassocketserver.Main.ROOT_PATH + "\\classes\\resources\\settings\\SocketServer.ini";
    private final String pathLogFiles = com.github.juliansantosinfo.jassocketserver.Main.ROOT_PATH +  "\\classes\\resources\\logs/";

    private int port;
    private int connectionLimit;
    private String logPath;

    // CONMTRUCTORS
    // -----------------------------------------------------------------------
    public ServerConfig() {
    }

    // GETTERS AND SETTERS
    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     *
     * @return
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }

    /**
     *
     * @param connectionLimit
     */
    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

    /**
     *
     * @return
     */
    public String getLogPath() {
        return logPath;
    }

    /**
     *
     * @param logPath
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    // USER METHODS
    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public boolean checkFolders() {
        boolean foldersIsOk = false;
        return foldersIsOk;
    }

    /**
     *
     * @return
     */
    public boolean loadIniFile() {

        boolean loadSuccessfully = false;
        File fileIni = new File(pathIniFile);

        if (!fileIni.exists()) {

        }

        try {
            Preferences ini = new IniPreferences(new Ini(fileIni));
            port = ini.node("server").getInt("port", 27000);
            connectionLimit = ini.node("server").getInt("connection_limit", 0);
            logPath = ini.node("server").get("log_path", pathLogFiles);
            logPath = logPath.isEmpty() ? pathLogFiles : logPath;
            loadSuccessfully = true;
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(
                    null,
                    "Failed to load ServerSocket.ini configuration file!\n" + ex.getMessage(),
                    "loadIniFile:Failed",
                    JOptionPane.ERROR_MESSAGE);
            loadSuccessfully = false;
        }
        return loadSuccessfully;
    }

}
