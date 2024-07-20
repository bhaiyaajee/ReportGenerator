package com.example.report.reportGenerator.service;

import com.example.report.reportGenerator.config.TransformationRulesConfig;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileProcessorService {

    @Autowired
    private TransformationRulesConfig transformationRulesConfig;
    
    private List<String[]> processedData = new ArrayList<>();

    private static final String INPUT_CSV_HEADER = "field1,field2,field3,field4,field5,refkey1,refkey2";
    private static final String REFERENCE_CSV_HEADER = "refkey1,refdata1,refkey2,refdata2,refdata3,refdata4";
    private static final Logger logger = LoggerFactory.getLogger(FileProcessorService.class);

    @Async
    public void processFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                logger.info("Processing file: {}", file.getOriginalFilename());
                String[] headers = csvReader.readNext(); 

                int field1Index = findIndex(headers, "field1");
                int field2Index = findIndex(headers, "field2");
                int field3Index = findIndex(headers, "field3");
                int field4Index = findIndex(headers, "field4");
                int field5Index = findIndex(headers, "field5");
                int refkey1Index = findIndex(headers, "refkey1");
                int refkey2Index = findIndex(headers, "refkey2");

                String[] nextRecord;
                while ((nextRecord = csvReader.readNext()) != null) {
                    String field1 = nextRecord[field1Index];
                    String field2 = nextRecord[field2Index];
                    String field3 = nextRecord[field3Index];
                    String field4 = nextRecord[field4Index];
                    String field5 = nextRecord[field5Index];
                    String refkey1 = nextRecord[refkey1Index];
                    String refkey2 = nextRecord[refkey2Index];

                    String outfield1 = applyTransformationRule(transformationRulesConfig.getOutfield1Rule(), nextRecord);
                    String outfield2 = applyTransformationRule(transformationRulesConfig.getOutfield2Rule(), nextRecord);
                    String outfield3 = applyTransformationRule(transformationRulesConfig.getOutfield3Rule(), nextRecord);
                    String outfield4 = applyTransformationRule(transformationRulesConfig.getOutfield4Rule(), nextRecord);
                    String outfield5 = applyTransformationRule(transformationRulesConfig.getOutfield5Rule(), nextRecord);

                    processedData.add(new String[]{outfield1, outfield2, outfield3, outfield4, outfield5});

      }
            } catch (IOException | CsvValidationException e) {
            logger.error("Error processing file", e);
            e.printStackTrace();
        }
    }

    private int findIndex(String[] headers, String fieldName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(fieldName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Header not found: " + fieldName);
    }

    private String applyTransformationRule(String rule, String[] record) {

        if (rule.startsWith("CONCAT")) {
            String[] fields = extractFields(rule);
            return record[findIndex(record, fields[0])] + record[findIndex(record, fields[1])];
        } else if (rule.startsWith("MAX")) {
            String[] fields = extractFields(rule);
            double value1 = Double.parseDouble(record[findIndex(record, fields[0])]);
            double value2 = Double.parseDouble(record[findIndex(record, fields[1])]);
            return Double.toString(Math.max(value1, value2));
        } else {
          
            throw new IllegalArgumentException("Unsupported rule: " + rule);
        }
    }

    private String[] extractFields(String rule) {
  
        int startIndex = rule.indexOf('(') + 1;
        int endIndex = rule.indexOf(')');
        String fields = rule.substring(startIndex, endIndex);
        return fields.split(",\\s*");
    }

    public void generateReport(String outputPath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
          
            writer.writeNext(new String[]{"outfield1", "outfield2", "outfield3", "outfield4", "outfield5"});
            writer.writeAll(processedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public List<String[]> getProcessedData() {
		return null;
	}

    public void setProcessedData(List<String[]> processedData2) {
    }
}
