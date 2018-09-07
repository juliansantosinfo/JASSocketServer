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
package com.github.juliansantosinfo.jassocketserver;

import com.github.juliansantosinfo.jassocketserver.client.Client;
import com.github.juliansantosinfo.jassocketserver.server.Server;
import java.io.File;

/**
 *
 * @author Julian A. Santos
 */
public class Main {
    
    public static final File JAR_FILE = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    public static final String ROOT_PATH = JAR_FILE.getParent();
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        new Server();
        
        for (int i = 0; i < 1000; i++) {
            new Client().connect("127.0.0.1", 27000);
        }
        
    }

}
