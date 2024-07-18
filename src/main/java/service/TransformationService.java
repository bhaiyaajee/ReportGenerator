@Service
public class TransformationService {

    @Autowired
    private TransformationRulesConfig rulesConfig;

    public OutputData transform(InputData inputData, ReferenceData referenceData) {
        // Apply transformation rules based on rulesConfig
        OutputData outputData = new OutputData();
        outputData.setOutfield1(inputData.getField1() + inputData.getField2());
        outputData.setOutfield2(referenceData.getRefdata1());
        outputData.setOutfield3(referenceData.getRefdata2() + referenceData.getRefdata3());
        outputData.setOutfield4(inputData.getField3() * Math.max(inputData.getField5(), referenceData.getRefdata4()));
        outputData.setOutfield5(Math.max(inputData.getField5(), referenceData.getRefdata4()));
        return outputData;
    }
}
