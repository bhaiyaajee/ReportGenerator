package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.example.report.reportGenerator.controller.FileProcessingController;
import com.example.report.reportGenerator.service.FileProcessorService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FileProcessingControllerTest {

    @InjectMocks
    private FileProcessingController fileProcessingController;

    @Mock
    private FileProcessorService fileProcessorService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileProcessingController).build();
    }

    @Test
    public void testUploadFileSuccess() throws Exception {
        String csvData = "field1,field2,field3,field4,field5,refkey1,refkey2\n" +
                         "value1_1,value1_2,value1_3,value1_4,10.5,key1_1,key1_2\n" +
                         "value2_1,value2_2,value2_3,value2_4,15.3,key2_1,key2_2\n";
        MultipartFile mockFile = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvData.getBytes(StandardCharsets.UTF_8));

        doNothing().when(fileProcessorService).processFile(mockFile);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                .file((MockMultipartFile) mockFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File uploaded and processed successfully"));

        verify(fileProcessorService, times(1)).processFile(mockFile);
    }

    @Test
    public void testUploadFileFailure() throws Exception {
        String csvData = "field1,field2,field3,field4,field5,refkey1,refkey2\n" +
                         "value1_1,value1_2,value1_3,value1_4,10.5,key1_1,key1_2\n" +
                         "value2_1,value2_2,value2_3,value2_4,15.3,key2_1,key2_2\n";
        MultipartFile mockFile = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvData.getBytes(StandardCharsets.UTF_8));

        doThrow(new RuntimeException("Processing failed")).when(fileProcessorService).processFile(mockFile);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                .file((MockMultipartFile) mockFile))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Failed to process file: Processing failed"));

        verify(fileProcessorService, times(1)).processFile(mockFile);
    }

    @Test
    public void testGenerateReportSuccess() throws Exception {
        doNothing().when(fileProcessorService).generateReport(anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/generate-report")
                .param("outputPath", "test-output.csv"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Report generated successfully at test-output.csv"));

        verify(fileProcessorService, times(1)).generateReport("test-output.csv");
    }

    @Test
    public void testGenerateReportFailure() throws Exception {
 
        doThrow(new RuntimeException("Report generation failed")).when(fileProcessorService).generateReport(anyString());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/generate-report")
                .param("outputPath", "test-output.csv"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Failed to generate report: Report generation failed"));

        verify(fileProcessorService, times(1)).generateReport("test-output.csv");
    }

}

