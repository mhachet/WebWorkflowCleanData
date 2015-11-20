package fr.bird.bloom.dto;

import fr.bird.bloom.beans.Finalisation;
import fr.bird.bloom.stepresults.Step1_MappingDwc;
import fr.bird.bloom.stepresults.Step2_ReconciliationService;
import fr.bird.bloom.stepresults.Step3_CheckCoordinates;
import fr.bird.bloom.stepresults.Step4_CheckGeoIssue;
import fr.bird.bloom.stepresults.Step5_IncludeSynonym;
import fr.bird.bloom.stepresults.Step6_CheckTDWG;
import fr.bird.bloom.stepresults.Step7_CheckISo2Coordinates;
import fr.bird.bloom.stepresults.Step8_CheckCoordinatesRaster;
import fr.bird.bloom.stepresults.Step9_EstablishmentMeans;

@SuppressWarnings("unused") // unused getters needed for json serialization
public class WorkflowResults {
    public Step1_MappingDwc getStep1Result() {
        return step1Result;
    }

    public Step2_ReconciliationService getStep2Result() {
        return step2Result;
    }

    public Step3_CheckCoordinates getStep3Result() {
        return step3Result;
    }

    public Step4_CheckGeoIssue getStep4Result() {
        return step4Result;
    }

    public Step5_IncludeSynonym getStep5Result() {
        return step5Result;
    }

    public Step6_CheckTDWG getStep6Result() {
        return step6Result;
    }

    public Step7_CheckISo2Coordinates getStep7Result() {
        return step7Result;
    }

    public Step8_CheckCoordinatesRaster getStep8Result() {
        return step8Result;
    }

    public Step9_EstablishmentMeans getStep9Result() {
        return step9Result;
    }

    public Finalisation getFinalisation() {
        return finalisation;
    }

    private Step1_MappingDwc step1Result;
    private Step2_ReconciliationService step2Result;
    private Step3_CheckCoordinates step3Result;
    private Step4_CheckGeoIssue step4Result;
    private Step5_IncludeSynonym step5Result;
    private Step6_CheckTDWG step6Result;
    private Step7_CheckISo2Coordinates step7Result;
    private Step8_CheckCoordinatesRaster step8Result;

    private Step9_EstablishmentMeans step9Result;

    private Finalisation finalisation;

    private WorkflowResults(Step1_MappingDwc step1Result,
                            Step2_ReconciliationService step2Result,
                            Step3_CheckCoordinates step3Result,
                            Step4_CheckGeoIssue step4Result,
                            Step5_IncludeSynonym step5Result,
                            Step6_CheckTDWG step6Result,
                            Step7_CheckISo2Coordinates step7Result,
                            Step8_CheckCoordinatesRaster step8Result,
                            Step9_EstablishmentMeans step9Result, Finalisation finalisation) {
        this.step1Result = step1Result;
        this.step2Result = step2Result;
        this.step3Result = step3Result;
        this.step4Result = step4Result;
        this.step5Result = step5Result;
        this.step6Result = step6Result;
        this.step7Result = step7Result;
        this.step8Result = step8Result;
        this.step9Result = step9Result;
        this.finalisation = finalisation;
    }

    public static WorkflowResultsBuilder builder(){
        return new WorkflowResultsBuilder();
    }

    public static class WorkflowResultsBuilder {

        private WorkflowResultsBuilder(){}

        private Step1_MappingDwc step1Result;
        private Step2_ReconciliationService step2Result;
        private Step3_CheckCoordinates step3Result;
        private Step4_CheckGeoIssue step4Result;
        private Step5_IncludeSynonym step5Result;
        private Step6_CheckTDWG step6Result;
        private Step7_CheckISo2Coordinates step7Result;
        private Step8_CheckCoordinatesRaster step8Result;
        private Step9_EstablishmentMeans step9Result;
        private Finalisation finalisation;

        public WorkflowResultsBuilder setStep1Result(Step1_MappingDwc step1Result) {
            this.step1Result = step1Result;
            return this;
        }

        public WorkflowResultsBuilder setStep2Result(Step2_ReconciliationService step2Result) {
            this.step2Result = step2Result;
            return this;
        }

        public WorkflowResultsBuilder setStep3Result(Step3_CheckCoordinates step3Result) {
            this.step3Result = step3Result;
            return this;
        }

        public WorkflowResultsBuilder setStep4Result(Step4_CheckGeoIssue step4Result) {
            this.step4Result = step4Result;
            return this;
        }

        public WorkflowResultsBuilder setStep5Result(Step5_IncludeSynonym step5Result) {
            this.step5Result = step5Result;
            return this;
        }

        public WorkflowResultsBuilder setStep6Result(Step6_CheckTDWG step6Result) {
            this.step6Result = step6Result;
            return this;
        }

        public WorkflowResultsBuilder setStep7Result(Step7_CheckISo2Coordinates step7Result) {
            this.step7Result = step7Result;
            return this;
        }

        public WorkflowResultsBuilder setStep8Result(Step8_CheckCoordinatesRaster step8Result) {
            this.step8Result = step8Result;
            return this;
        }

        public WorkflowResultsBuilder setStep9Result(Step9_EstablishmentMeans step9Result) {
            this.step9Result = step9Result;
            return this;
        }

        public WorkflowResultsBuilder setFinalisation(Finalisation finalisation) {
            this.finalisation = finalisation;
            return this;
        }

        public WorkflowResults build() {
            return new WorkflowResults(
                    step1Result, step2Result, step3Result,
                    step4Result,step5Result, step6Result,
                    step7Result, step8Result, step9Result,
                    finalisation);
        }
    }
}
