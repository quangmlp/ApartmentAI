package com.bluemoonproject.service;

import com.bluemoonproject.entity.Fee;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.wml.Document;
import org.springframework.stereotype.Service;

import java.io.*;


import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.XmlUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class BillGenerationService {

    public byte[] generateInvoiceFromTemplate(Fee fee) throws Exception {
        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("templates/invoice_template.docx")) {
            if (templateInputStream == null) {
                throw new FileNotFoundException("Template file not found in resources/templates");
            }

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

            VariablePrepare.prepare(wordMLPackage);

            Map<String, String> variables = new HashMap<>();
            variables.put("roomNumber", fee.getRoomNumber());
            variables.put("description", fee.getDescription());
            variables.put("amount", String.format("%.0f VND", fee.getAmount()));
            variables.put("createdAt", fee.getCreatedAt().toLocalDate().toString());
            variables.put("dueDate", fee.getDueDate().toString());

            wordMLPackage.getMainDocumentPart().variableReplace(variables);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wordMLPackage.save(baos);
            return baos.toByteArray();
        }
    }



}
