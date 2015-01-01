/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.core;

import java.io.File;

/**
 *
 * @author Theodore Dubois
 */
public final class Constants {
    /**
     * If no HTTP version is specified by the client, this is used.
     */
    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    /**
     * The home directory for the server.
     */
    public static File HOME;
    
    private Constants() {}
}
