/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.logica;

import com.davivienda.snc.documents.templates.request.gui.model.dto.BuildParametersRequestDto;
import com.davivienda.snc.documents.templates.request.gui.model.exceptions.ApplicationException;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author MAAACOST
 */
public class RequestTemplateGenerarPaquete extends RequestTemplate {

    @Override
    public List<String> getListFieldsResource(File file) throws ApplicationException {
        return getListFieldsFolder(file);
    }
    
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

    }
    
    
    @Override
    public List<String> fixListFields(List<String> listField) throws ApplicationException {
        List<String> result = new ArrayList<>();
        for (String campoItem : listField) {
            if( isAComment(campoItem)){
                result.add(campoItem);
                continue;
            }
            
            final String prefix = "<valParametro id=\"";
            final String midfix01 = "\" valor=\"";
            final String postfix = "\"/>";

            final String commentVacio = "  <!--Opcional-->";
            final String commentCheck = "  <!--Solo enviar check.png o uncheck.png, se envía uno de los dos-->";

            final String borrar02 = "VACIO";
            final String borrar03 = "uncheck.png";
            final String ident = "					";

            final String campoFix = campoItem.contains(",")? (campoItem.split(",")[0])+"}":campoItem;
            //final String campoFix2 = campoFix.replace("{", "").replace("}", "");
            final String campoValor = getFieldValue( campoFix, campoItem);

            String request = prefix + campoFix + midfix01 + campoValor + postfix;
            if (campoItem.contains(borrar02)) {
                request += commentVacio;
            }

            if (campoItem.contains(borrar03)) {
                request += commentCheck;
            }
            
            request = ident+request;
            result.add(request);
        }
        if(null==result || result.isEmpty()){
            throw new ApplicationException("No se construyo ningun parametro");
        }
        
        return result;
    }

    @Override
    public List<String> readRequestGuide() throws ApplicationException {

        InputStream targetStream = getClass().getClassLoader().getResourceAsStream("resources/requestSrvScnGeneracionPaqueteDocumentos.xml");
        Scanner sc = new Scanner(targetStream, StandardCharsets.UTF_8.name());
        List<String> listResult = new ArrayList<>();
        while (sc.hasNextLine()) {
            String linea = sc.hasNextLine() ? sc.nextLine() : "";
            listResult.add(linea);
        }
        return listResult;
    }
    
    @Override
    public List<String> buildRequest(BuildParametersRequestDto buildParametersRequestDto )throws ApplicationException {
        
        List<String> listResult = new ArrayList<>();
        
        for(String guide:buildParametersRequestDto.getListRequestGuide()){
            if(guide.contains("${parametros}")){
                listResult.addAll(buildParametersRequestDto.getListParameters());
                continue;
            }
            listResult.add(guide);
        }
        return listResult;
    }

    
    
    
   
}
