@Service
public class ReportGenerationService {

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1 AM
    public void generateReport() {
        // Implement report generation logic
        // Iterate over input files, transform data, and write to output
    }
}
