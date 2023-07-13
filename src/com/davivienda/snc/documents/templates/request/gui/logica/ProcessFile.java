/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.logica;

import com.davivienda.snc.documents.templates.request.gui.logica.utils.FileUtils;
import com.davivienda.snc.documents.templates.request.gui.logica.utils.ReportUtils;
import com.davivienda.snc.documents.templates.request.gui.model.dto.BuildParametersRequestDto;
import com.davivienda.snc.documents.templates.request.gui.model.dto.ViewParametersDto;
import com.davivienda.snc.documents.templates.request.gui.model.enums.TypeRequestEnum;
import com.davivienda.snc.documents.templates.request.gui.model.exceptions.ApplicationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.Desktop;
import org.apache.log4j.Logger;

/**
 *
 * @author MAAACOST
 */
public class ProcessFile {
    
    private final static Logger logger = Logger.getLogger(ProcessFile.class);

    private static final String FILE_NAME_OUT_REQUEST = "builtFileRequest.txt";
    private static final String FILE_NAME_OUT_PDF = "builtFilePDF.pdf";

    private RequestTemplate requestTemplate;

    public void buildRequestTemplate(File file, ViewParametersDto viewParametersDto ) throws ApplicationException {
        
        
        requestTemplate = getInstanceRequestTemplate(viewParametersDto);
        
        List<String> listFields = requestTemplate.getListFieldsResource(file);
        List<String> listParameters = requestTemplate.fixListFields(listFields);
        List<String> listRequestGuide = requestTemplate.readRequestGuide();
        BuildParametersRequestDto buildParametersRequestDto = new BuildParametersRequestDto();
        buildParametersRequestDto.setListParameters(listParameters);
        buildParametersRequestDto.setListRequestGuide(listRequestGuide);
        buildParametersRequestDto.setFile(file);
        List<String> listBuildRequest = requestTemplate.buildRequest(buildParametersRequestDto);
        
        
        FileUtils.deleteFile(FILE_NAME_OUT_REQUEST);
        try {
            for (String lineRequest : listBuildRequest) {
                FileUtils.writeLineInFile(lineRequest, FILE_NAME_OUT_REQUEST);
            }
            Desktop.getDesktop().open(new File(FILE_NAME_OUT_REQUEST));
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new ApplicationException("Error generando Request. " + ex.getMessage());
        }
       
        if (!TypeRequestEnum.SRV_SCN_GENERACION_REPORTE.equals(viewParametersDto.getTypeRequestEnum())) {
            return;
        }
        
        ReportUtils reportUtils = new ReportUtils();
         try {
            FileUtils.deleteFile(FILE_NAME_OUT_PDF);
            byte[] pdf = reportUtils.crearReporte(file);
            FileUtils.writeByteArrayToFile(pdf, FILE_NAME_OUT_PDF);
            Desktop.getDesktop().open(new File(FILE_NAME_OUT_PDF));
        } catch (IOException ex) {
            logger.error(ex.getMessage(),ex);
            throw new ApplicationException("Error generando PDF. " + ex.getMessage());
        }

    }
    
    private RequestTemplate getInstanceRequestTemplate(ViewParametersDto viewParametersDto)throws ApplicationException{
        if(null==viewParametersDto  || null == viewParametersDto.getTypeRequestEnum()){
            throw new ApplicationException("Error. No existe seleccion de tipo de request");
        }
        if( viewParametersDto.getTypeRequestEnum().equals(TypeRequestEnum.SRV_SCN_GENERACION_REPORTE)){
            return new RequestTemplateGenerarReporte();
        }
        if( viewParametersDto.getTypeRequestEnum().equals(TypeRequestEnum.SRV_SCN_GENERACION_PAQUETE_DOCUMENTOS )){
            return new RequestTemplateGenerarPaquete();
        }
        return new RequestTemplateDesembolso();
    }
    
    

    /**
     * @return the requestTemplate
     */
    public RequestTemplate getRequestTemplate() {
        return requestTemplate;
    }

   

}
