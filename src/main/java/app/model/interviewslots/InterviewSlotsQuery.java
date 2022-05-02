package app.model.interviewslots;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class InterviewSlotsQuery {
    private String candidateName;
    private List<String> interviewersNames;

    public InterviewSlotsQuery(){}

    public InterviewSlotsQuery(Builder builder) {
        this.candidateName = builder.candidateName;
        this.interviewersNames = builder.interviewersNames;
    }

    public static class Builder {
        private String candidateName;
        private List<String> interviewersNames;

        public static Builder interviewSlotsQueryModelWith() {
            return new Builder();
        }

        public Builder withCandidateName(String candidateName) {
            this.candidateName = candidateName;

            return this;
        }

        public Builder withInterviewersNames(List<String> interviewersNames) {
            this.interviewersNames = interviewersNames;

            return this;
        }

        public InterviewSlotsQuery build() {
            return new InterviewSlotsQuery(this);
        }
    }
}
