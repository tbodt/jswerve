/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tbodt.jswerve.examples.name;

import com.tbodt.jswerve.*;
import java.util.regex.Pattern;

/**
 *
 * @author Theodore Dubois
 */
public class NameDisplayPage extends PatternPage {
    public NameDisplayPage() {
        super(Request.Method.GET, Pattern.compile("/(\\w)+/?"));
    }

    @Override
    public Response service(Request request) {
        return null;
    }
}
