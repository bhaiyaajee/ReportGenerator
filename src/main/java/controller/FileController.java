@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportGenerationService reportGenerationService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    @PostMapping("/generate-report")
    public ResponseEntity<String> generateReport() {
        reportGenerationService.generateReport();
        return ResponseEntity.ok("Report generation triggered successfully.");
    }
}
