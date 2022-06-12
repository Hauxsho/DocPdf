package com.example.demo.service;


import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

@Service
public class DocPdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pdf.directory}")
    private String pdfDirectory ;



    int count = 0;
    ITextRenderer renderer;
    FileOutputStream fileOutputStream;
    Context context;



    public void generateFile(String templateName, Map<String, Object> s) throws FileNotFoundException {

        if(count==0){
        renderer = new ITextRenderer();
        fileOutputStream = new FileOutputStream(pdfDirectory + File.separator+ "DocToPdf.pdf"); //specifing location and name of pdf FIle
        context = new Context();}

        //Path path = Paths.get("src/main/resources/static/images/TestImg.png");
        //String base64Img = convert64(path);
        // this will add data to our variable holders in html template
        //context.setVariable("image", path.toUri().toString());-

        for(Map.Entry<String, Object> entry : s.entrySet())
        {
            context.setVariable(entry.getKey(), entry.getValue());
        }


        String htmlContent = templateEngine.process(templateName, context);

        try {

            renderer.setDocumentFromString(htmlContent); //rendering it from to html
            renderer.layout();

            if(count == 0) {
                renderer.createPDF(fileOutputStream, false);
                count++;
                //creating pdf in file
            }
            else {
                renderer.writeNextDocument();
            }

        }
        //Handling exceptions
        catch (DocumentException e) {
            System.out.println(e.getMessage() + "Doc");
        }

    }

    public void finishGenerating()
    {
        System.out.println("Got Called");
        renderer.finishPDF();
    }

}
