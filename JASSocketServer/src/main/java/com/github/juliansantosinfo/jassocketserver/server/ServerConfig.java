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

    private final String rootPath = com.github.juliansantosinfo.jassocketserver.Main.ROOT_PATH;
    private final String pathIniFile = rootPath + "\\JASSocketServer.ini";
    private final File iniFile = new File(pathIniFile);
    private String pathLogFiles;
    private int port;
    private int connectionLimit;
    private String logPath;

    // GETTERS AND SETTERS
    // -----------------------------------------------------------------------
    /**
     * Get port connection with server.
     *
     * @return host port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set port connection with server.
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get limit of clients connected to server.
     *
     * @return max conextions.
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }

    /**
     * Set limit of clients connected to server.
     *
     * @param connectionLimit
     */
    public void setConnectionLimit(int connectionLimit) {
        this.connectionLimit = connectionLimit;
    }

    /**
     * Get storage path of the log files.
     *
     * @return log files path.
     */
    public String getLogPath() {
        return logPath;
    }

    /**
     * Set storage path of the log files.
     *
     * @param logPath
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    // OTHERS METHODS
    // -----------------------------------------------------------------------
    /**
     * Create initialization file.
     *
     * @return created successfully.
     */
    public boolean createIniFile() {

        boolean createdSuccessfully = false;

        // Verifies that the file exists.
        if (!iniFile.exists()) {

            try {

                iniFile.createNewFile();

                Ini ini = new Ini(iniFile);

                // Set SERVER Section.
                ini.put("SERVER", "port", 1);
                ini.put("SERVER", "connection_limit", 1000);
                ini.put("SERVER", "log_path", false);

                // Store sections.
                ini.store();

                createdSuccessfully = true;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to create JASServerSocket.ini configuration file!\n" + ex.getMessage(),
                        "CreateIniFile:Failed",
                        JOptionPane.ERROR_MESSAGE);
                createdSuccessfully = false;
            }
        }

        return createdSuccessfully;
    }

    /**
     * Load information from the initialization file.
     *
     * @return loaded successfully.
     */
    public boolean loadIniFile() {

        // Variables.
        boolean loadedSuccessfully;
        File fileIni;

        // Create file structure if it does not exist.
        checkStructFiles();

        // Initialize variables.
        loadedSuccessfully = false;
        fileIni = new File(pathIniFile);

        // Try load ini file.
        try {
            Preferences ini = new IniPreferences(new Ini(fileIni));
            port = ini.node("SERVER").getInt("port", 27000);
            connectionLimit = ini.node("SERVER").getInt("connection_limit", 0);
            logPath = ini.node("SERVER").get("log_path", pathLogFiles);
            logPath = logPath.isEmpty() ? pathLogFiles : logPath;
            loadedSuccessfully = true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to load JASServerSocket.ini configuration file!\n" + ex.getMessage(),
                    "loadIniFile:Failed",
                    JOptionPane.ERROR_MESSAGE);
            loadedSuccessfully = false;
        }

        return loadedSuccessfully;
    }

    /**
     * Validate file structure for the project. If it does not exist create the
     * directories and archives.
     */
    public void checkStructFiles() {

        File logPath = new File(rootPath + "\\logs\\");
        File dataPath = new File(rootPath + "\\data\\");

        if (!logPath.exists()) {
            logPath.mkdir();
        }

        if (!dataPath.exists()) {
            dataPath.mkdir();
        }

        pathLogFiles = logPath.getParent();

        createIniFile();

    }

}
