/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.logica;

import com.davivienda.snc.documents.templates.request.gui.model.dto.BuildParametersRequestDto;
import com.davivienda.snc.documents.templates.request.gui.model.exceptions.ApplicationException;
import com.google.common.io.ByteSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author MAAACOST
 */
public abstract class RequestTemplate {
    
    public abstract List<String> getListFieldsResource(File file) throws ApplicationException; 
    
    protected List<String> getListFields(File template) throws ApplicationException {
        try {
            
            if(template.isDirectory()){
                throw new ApplicationException("Error.Se ha seleccionado una carpeta. Se debe seleccionar un archivo plantilla FOP");
            }
            
             if(!isTemplate(template)){
                throw new ApplicationException("Error. Se debe seleccionar un archivo plantilla FOP");
            }
            
            
            
            byte[] array = Files.readAllBytes(Paths.get(template.getAbsolutePath()));
            InputStream targetStream = ByteSource.wrap(array).openStream();
            Scanner sc = new Scanner(targetStream, StandardCharsets.UTF_8.name());
            List<String> listCampos = new ArrayList<>();
            while (sc.hasNextLine()) {
                String linea = sc.hasNextLine() ? sc.nextLine() : "";
                if (!linea.contains("{")) {
                    continue;
                }
                String campo = "";
                boolean getWord = false;
                for (int i = 0; i < linea.length(); i++) {
                    if (linea.charAt(i) == '{') {
                        getWord = true;
                    }
                    if (getWord) {
                        campo += linea.charAt(i);
                    }
                    if (linea.charAt(i) == '}') {
                        getWord = false;
                        if(!containsField(listCampos,campo)){
                            listCampos.add(campo);
                        }else{
                            listCampos = validarArrayItem(listCampos,campo);
                        }
                        campo = "";
                    }
                }
            }
            
            if (null !=  sc.ioException() ) {
                throw sc.ioException();
            }
            
            if(null==listCampos || listCampos.isEmpty()){
                throw new ApplicationException(String.format("La plantilla %s no contiene parametros del request", template.getName()));
            }
            
            return listCampos;
        } catch (IOException ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }
    
    
    protected Boolean isArrayItem(String campo){
        final String match = "item(?)";
        String campoTemp = campo.replace("1", "?")
                .replace("2", "?")
                .replace("3", "?")
                .replace("4", "?")
                .replace("5", "?")
                .replace("6", "?")
                .replace("7", "?")
                .replace("8", "?")
                .replace("9", "?");
        return campoTemp.contains(match);
    }
    
    
    protected List<String> validarArrayItem(List<String> listCampos,String campo){
        if(!isArrayItem(campo)){
            return listCampos;
        }
        List<String> listCamposTemp = new ArrayList<String>();
        for(String ca:listCampos){
            if(ca.contains(campo.split(",")[0])){
                listCamposTemp.add(campo);
                continue;
            }
            listCamposTemp.add(ca);
        }
        
       return listCamposTemp;
       
    }
    
    
    protected boolean containsField(List<String>listFields, String field){
        
        if(!field.contains(",")){
            return listFields.contains(field);
        }
        
        if (listFields.stream().anyMatch((fe) -> (fe.split(",")[0].equals(field.split(",")[0])))) {
            return true;
        }
        return false;
    }
    
    /*
    public List<String> getListFieldsFolder(File folder) throws ApplicationException {

        if (!folder.isDirectory()) {
            throw new ApplicationException("No se ha selccionado una carpeta");
        }

        if (folder.listFiles().length < 1) {
            throw new ApplicationException("La carpeta seleccionada no contiene archivos");
        }

        boolean containsFo = false;
        List<String> listResult = new ArrayList<>();
        for (File fileTemplate : folder.listFiles()) {
            if (fileTemplate.isDirectory()) {
                throw new ApplicationException("La carpeta seleccionada no debe contener subcarpetas");
            }

            if (!isTemplate(fileTemplate)) {
                continue;
            }
            containsFo = true;

            listResult.add(String.format("<!--Campos %s -->", fileTemplate.getName()));
            List<String> listFieldsTemplate = getListFields(fileTemplate);
            for (String field : listFieldsTemplate) {
                if (listResult.contains(field)) {
                    continue;
                }
                listResult.add(field);
            }
        }
        if (!containsFo) {
            throw new ApplicationException("La carpeta seleccionada no contiene plantillas FOP");
        }
        return listResult;

    }*/
    
    protected boolean isTemplate(File template) {
        return template.getName().contains(".fo");
    }
    
    public abstract List<String> fixListFields(List<String> listField)throws ApplicationException ;
    
    public abstract List<String> readRequestGuide()throws ApplicationException ;
    
    public abstract List<String> buildRequest(BuildParametersRequestDto buildParametersRequestDto )throws ApplicationException ;
    
    
    protected  boolean isAComment(String field){
        return !field.contains("{");
    }
    
    protected String getFieldValue(String fieldFix,String fieldItem){
        
        String out = fieldFix.replace("{", "").replace("}", "");
        
        if(fieldItem.contains("&formatearNumero")){
            return "0";
        }
        if(fieldItem.contains("&formatearFecha")){
            return "20230101";
        }
        if(fieldItem.contains("&numeroALetra")){
            return "12";
        }
        
        
        return out;
    }
    
    
    
}
