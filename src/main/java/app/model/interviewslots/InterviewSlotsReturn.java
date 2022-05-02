package app.model.interviewslots;

import app.model.utils.AvailabilitySlot;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class InterviewSlotsReturn {
    private String candidateName;
    private List<String> interviewersNames;
    private List<AvailabilitySlot> interviewAvailabilitySlotList;

    public InterviewSlotsReturn() {}

    public InterviewSlotsReturn(Builder builder) {
        this.candidateName = builder.candidateName;
        this.interviewersNames = builder.interviewersNames;
        this.interviewAvailabilitySlotList = builder.interviewAvailabilitySlotList;
    }

    public static class Builder {
        private String candidateName;
        private List<String> interviewersNames;
        private List<AvailabilitySlot> interviewAvailabilitySlotList;

        public static Builder interviewSlotsReturnModelWith() {
            return new Builder();
        }

        public Builder withCandidateName(String candidateName) {
            this.candidateName = candidateName;

            return this;
        }

        public Builder withInterviewerNameList(List<String> interviewersNames) {
            this.interviewersNames = interviewersNames;

            return this;
        }

        public Builder withInterviewAvailabilitySlotList(List<AvailabilitySlot> interviewAvailabilitySlotList) {
            this.interviewAvailabilitySlotList = interviewAvailabilitySlotList;

            return this;
        }

        public InterviewSlotsReturn build() {
            return new InterviewSlotsReturn(this);
        }
    }
}
