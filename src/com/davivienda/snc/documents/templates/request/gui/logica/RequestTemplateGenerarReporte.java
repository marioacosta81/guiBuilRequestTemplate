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
public class RequestTemplateGenerarReporte extends RequestTemplate {

    
    @Override
    public List<String> getListFieldsResource(File file) throws ApplicationException {
        return getListFields(file);
    }
    
    
    
    
    @Override
    public List<String> fixListFields(List<String> listField) throws ApplicationException {
        List<String> result = new ArrayList<>();
        for (String campoItem : listField) {
            if( isAComment(campoItem)){
                result.add(campoItem);
                continue;
            }
            Integer countItemsArray = getCountItemsArray(campoItem);
            
            final String prefix = "\t\t<ns0:valParametro id=\"";
            final String midfix01 = "\">\n";
            final String midfix02 = "\t\t\t<ns0:valor>";
            final String midfix03 = "</ns0:valor>\n";
            final String postfix = "\t\t</ns0:valParametro>";

            final String campoFix = campoItem.contains(",")? (campoItem.split(",")[0])+"}":campoItem; 
            final String campoNombre = campoFix.replace("{", "").replace("}", "");
            final String fieldValue = getFieldValue( campoFix, campoItem);
            boolean isFormat = campoItem.contains("&formatearNumero") || campoItem.contains("&formatearFecha");
            
            String campoValor = "";
            if(countItemsArray<2){
                campoValor = midfix02 + fieldValue+ midfix03;
            }else{
                for(int j=0;j<countItemsArray;j++){
                    if(isFormat){
                        campoValor += midfix02 + fieldValue+ midfix03;
                    }else{
                        campoValor += midfix02 + fieldValue +"_"+(j+1) + midfix03;
                    }
                }
            }
            
            

            String request = prefix + campoNombre + midfix01 + campoValor  + postfix;
            result.add(request);
        }
        if(null==result || result.isEmpty()){
            throw new ApplicationException("No se construyo ningun parametro");
        }
        
        return result;
    }
    
    private Integer getCountItemsArray(String campoItem) {
        if (!isArrayItem(campoItem)) {
            return 0;
        }
        String strNumero = "";
        String prefix = "";
        boolean getWord = false;
        for (int i = 0; i < campoItem.length(); i++) {
            if (prefix.contains(",,&item(")) {
                getWord = true;
            }
            if (getWord && campoItem.charAt(i) == ')') {
                try {
                    Integer itemsArray = Integer.parseInt(strNumero);
                    return itemsArray;
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            if (getWord) {
                strNumero += campoItem.charAt(i);
            }
            prefix += campoItem.charAt(i);
        }
        return 0;
    }
    
    
    
    
   

    @Override
    public List<String> readRequestGuide() throws ApplicationException {

        InputStream targetStream = getClass().getClassLoader().getResourceAsStream("resources/requestSrvScnGenerarReporte.xml");
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
            if(guide.contains("${nombre_plantilla}")){
                listResult.add( guide.replace("${nombre_plantilla}", buildParametersRequestDto.getFile().getName().replace(".fo","").replace(".FO", "")  ));
                continue;
            }
            listResult.add(guide);
        }
        return listResult;
    }

   
    
  

   
}
