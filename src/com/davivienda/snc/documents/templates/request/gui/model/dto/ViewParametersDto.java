/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.model.dto;

import com.davivienda.snc.documents.templates.request.gui.model.enums.TypeRequestEnum;

/**
 *
 * @author MAAACOST
 */
public class ViewParametersDto {
    
    private TypeRequestEnum typeRequestEnum;
    
    private String idNumberClient;
    
    private String emailTest;

    /**
     * @return the typeRequestEnum
     */
    public TypeRequestEnum getTypeRequestEnum() {
        return typeRequestEnum;
    }

    /**
     * @param typeRequestEnum the typeRequestEnum to set
     */
    public void setTypeRequestEnum(TypeRequestEnum typeRequestEnum) {
        this.typeRequestEnum = typeRequestEnum;
    }

    /**
     * @return the idNumberClient
     */
    public String getIdNumberClient() {
        return idNumberClient;
    }

    /**
     * @param idNumberClient the idNumberClient to set
     */
    public void setIdNumberClient(String idNumberClient) {
        this.idNumberClient = idNumberClient;
    }

    /**
     * @return the emailTest
     */
    public String getEmailTest() {
        return emailTest;
    }

    /**
     * @param emailTest the emailTest to set
     */
    public void setEmailTest(String emailTest) {
        this.emailTest = emailTest;
    }
    
    
    
    
    
    
    
}
