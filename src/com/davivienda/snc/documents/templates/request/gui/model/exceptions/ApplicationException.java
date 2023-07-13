/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.model.exceptions;

/**
 *
 * @author MAAACOST
 */
public class ApplicationException extends Exception {

    private String messaqe;

    public ApplicationException(String message) {
        super(message);
        this.messaqe = message;

    }
    
    public String getMessaqe() {
        return messaqe;
    }
}
