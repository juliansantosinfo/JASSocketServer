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
package com.github.juliansantosinfo.jassocketserver.message;

import com.github.juliansantosinfo.jasdatehour.JASDateHour;

/**
 *
 * @author Julian A. Santos
 */
public class Message {
    
    public static final int TYPE_DEBUG = '0';
    
    private String date;
    private String hour;
    private int typrMessage;
    private String message;
    private boolean messageReady;
    
    public Message(int typeMessage, String message) {
        this.date = "";
        this.hour = "";
        this.messageReady = false;
        this.typrMessage = typeMessage;
        this.message = message;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getTyprMessage() {
        return typrMessage;
    }

    public void setTyprMessage(int typrMessage) {
        this.typrMessage = typrMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessageReady() {
        return messageReady;
    }

    public void setMessageReady(boolean messageReady) {
        this.messageReady = messageReady;
    }

    public void read() {
        date = JASDateHour.currentDate();
        hour = JASDateHour.currentHour();
        messageReady = true;
    }
}
