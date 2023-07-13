/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.model.dto;

import java.io.File;
import java.util.List;

/**
 *
 * @author MAAACOST
 */
public class BuildParametersRequestDto {
    
    
    private List<String> listParameters; 
    private List<String> listRequestGuide;
    private File file;

    /**
     * @return the listParameters
     */
    public List<String> getListParameters() {
        return listParameters;
    }

    /**
     * @param listParameters the listParameters to set
     */
    public void setListParameters(List<String> listParameters) {
        this.listParameters = listParameters;
    }

    /**
     * @return the listRequestGuide
     */
    public List<String> getListRequestGuide() {
        return listRequestGuide;
    }

    /**
     * @param listRequestGuide the listRequestGuide to set
     */
    public void setListRequestGuide(List<String> listRequestGuide) {
        this.listRequestGuide = listRequestGuide;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

   
    
    
    
    
}
