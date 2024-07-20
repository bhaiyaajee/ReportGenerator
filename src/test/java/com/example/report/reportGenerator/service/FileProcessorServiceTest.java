package com.example.report.reportGenerator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.report.reportGenerator.config.TransformationRulesConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FileProcessorServiceTest {

    @InjectMocks
    private FileProcessorService fileProcessorService;

    @Mock
    private TransformationRulesConfig transformationRulesConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessFile() throws IOException {
       
        when(transformationRulesConfig.getOutfield1Rule()).thenReturn("CONCAT(field1, field2)");
        when(transformationRulesConfig.getOutfield2Rule()).thenReturn("refdata1");
        when(transformationRulesConfig.getOutfield3Rule()).thenReturn("CONCAT(refdata2, refdata3)");
        when(transformationRulesConfig.getOutfield4Rule()).thenReturn("field3 * MAX(field5, refdata4)");
        when(transformationRulesConfig.getOutfield5Rule()).thenReturn("MAX(field5, refdata4)");

        String csvData = "field1,field2,field3,field4,field5,refkey1,refkey2\n" +
                         "value1_1,value1_2,value1_3,value1_4,10.5,key1_1,key1_2\n" +
                         "value2_1,value2_2,value2_3,value2_4,15.3,key2_1,key2_2\n";
        MultipartFile mockFile = new MockMultipartFile("test.csv", csvData.getBytes());

        fileProcessorService.processFile(mockFile);

        List<String[]> processedData = fileProcessorService.getProcessedData();
        assertEquals(2, processedData.size());


        String[] record1 = processedData.get(0);
        assertEquals("value1_1value1_2", record1[0]); 
        assertEquals("refdata1", record1[1]);        
        assertEquals("value1_2value1_3", record1[2]); 
        assertEquals("value1_3 * 15.3", record1[3]);  
        assertEquals("15.3", record1[4]);             

        String[] record2 = processedData.get(1);
        assertEquals("value2_1value2_2", record2[0]);
        assertEquals("refdata1", record2[1]);
        assertEquals("value2_2value2_3", record2[2]);
        assertEquals("value2_3 * 15.3", record2[3]);
        assertEquals("15.3", record2[4]);

    }

    @Test
    public void testGenerateReport() throws IOException {
        
        List<String[]> processedData = List.of(
                new String[]{"value1_1value1_2", "refdata1", "value1_2value1_3", "value1_3 * 15.3", "15.3"},
                new String[]{"value2_1value2_2", "refdata1", "value2_2value2_3", "value2_3 * 15.3", "15.3"}
        );
        fileProcessorService.setProcessedData(processedData);

        String outputPath = "test-output.csv";
        fileProcessorService.generateReport(outputPath);

        BufferedReader reader = new BufferedReader(new StringReader(outputPath));
        String[] headers = reader.readLine().split(",");
        assertEquals("outfield1", headers[0]);
        assertEquals("outfield2", headers[1]);
        assertEquals("outfield3", headers[2]);
        assertEquals("outfield4", headers[3]);
        assertEquals("outfield5", headers[4]);

        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            assertEquals(processedData.get(lineCount)[0], fields[0]); 
            assertEquals(processedData.get(lineCount)[1], fields[1]); 
            assertEquals(processedData.get(lineCount)[2], fields[2]); 
            assertEquals(processedData.get(lineCount)[3], fields[3]);
            assertEquals(processedData.get(lineCount)[4], fields[4]);
            lineCount++;
        }
        reader.close();

    }

}

