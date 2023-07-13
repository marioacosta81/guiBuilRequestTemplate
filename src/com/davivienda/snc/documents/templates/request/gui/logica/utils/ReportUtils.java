/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.davivienda.snc.documents.templates.request.gui.logica.utils;

import com.davivienda.snc.documents.templates.request.gui.model.exceptions.ApplicationException;
import com.google.common.io.ByteSource;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.FopFactoryConfig;
import org.xml.sax.SAXException;

/**
 *
 * @author MAAACOST
 */
public class ReportUtils {
    private FopFactory fopFactory = null;
    private FOUserAgent foUserAgent = null;
    
    public byte[] crearReporte( File plantilla ) throws ApplicationException{
 
        try {
            
           
            byte[] file = fixTemplateToPreRender(plantilla);
            String encoding = "UTF8";
            String strContenidoPlantilla = new String(file, encoding);
            
            //URL url = getClass().getClassLoader().getResource("resources/fop.xconf");
            //InputStream inputStream =this.getClass().getClassLoader().getResourceAsStream("resources/fop.xconf");
            //this.fopFactory = FopFactory.newInstance( url.toURI(),inputStream);
            this.fopFactory = FopFactory.newInstance(new File("fopConf\\fop.xconf"));
            
            this.foUserAgent = this.fopFactory.newFOUserAgent();
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            ByteArrayOutputStream outByteArray = new ByteArrayOutputStream(100);
            OutputStream out = new BufferedOutputStream(outByteArray);
            Fop fop = this.fopFactory.newFop("application/pdf", this.foUserAgent, out);
            Result res = new SAXResult(fop.getDefaultHandler());
            
            Source src = new StreamSource(new StringReader(strContenidoPlantilla));

            transformer.transform(src, res);
            
            out.close();
            
            return outByteArray.toByteArray();
           
        }catch (SAXException | IOException | TransformerException /*| URISyntaxException*/ ex) {
            throw new ApplicationException("Error generando PDF. " + ex.getMessage());
        } 

    }
    
    private byte[] fixTemplateToPreRender(File template) throws ApplicationException {
        try {
            
            byte[] array = Files.readAllBytes(Paths.get(template.getAbsolutePath()));
            InputStream targetStream = ByteSource.wrap(array).openStream();
            Scanner sc = new Scanner(targetStream, StandardCharsets.UTF_8.name());
            StringBuilder sb = new StringBuilder();
            boolean getWord = false;
            boolean function = false;
            while (sc.hasNextLine()) {
                String linea = sc.hasNextLine() ? sc.nextLine() : "";
                if (!linea.contains("{")  &&  !linea.contains("}") ) {
                    sb.append(linea+"\n");
                    continue;
                }
                String lineaFix = "";
                
                for (int i = 0; i < linea.length(); i++) {
                    if (linea.charAt(i) == '{') {
                        getWord = true;
                    }
                    if (getWord && linea.charAt(i) == ',') {
                        function = true;
                    }
                    if (linea.charAt(i) == '}') {
                        getWord = false;
                        function = false;
                        lineaFix+=",,";
                    }
                    if (getWord && function) {
                        continue;
                    }
                    
                    lineaFix+=linea.charAt(i);
                }
                sb.append(lineaFix+"\n");
            }
            
           
            return sb.toString().getBytes();
        } catch (IOException ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }
    
    
    
    
    
    
}
