package app.model.interviewer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "interviewer")
@Getter
public class Interviewer {
    @Id
    private String name;

    @JsonIgnore
    @OneToOne(mappedBy = "interviewerModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private InterviewerAvailability interviewerAvailabilityModel;

    public Interviewer(){}

    public Interviewer(String name) {
        this.name = name;
    }

    public Interviewer(Builder builder) {
        this.name = builder.name;
        this.interviewerAvailabilityModel = builder.interviewerAvailabilityModel;
    }

    public static class Builder {
        private String name;
        private InterviewerAvailability interviewerAvailabilityModel;

        public static Builder interviewerModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withInterviewerAvailabilityModel(InterviewerAvailability interviewerAvailabilityModel) {
            this.interviewerAvailabilityModel = interviewerAvailabilityModel;

            return this;
        }

        public Interviewer build() {
            return new Interviewer(this);
        }
    }
}
