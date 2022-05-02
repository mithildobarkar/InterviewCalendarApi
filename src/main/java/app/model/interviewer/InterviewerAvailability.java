package app.model.interviewer;

import app.model.utils.AvailabilitySlot;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "interviewer_availability")
@Getter
public class InterviewerAvailability {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JsonProperty("interviewerName")
    @JoinColumn(name = "name", nullable = false)
    private Interviewer interviewerModel;

    @NotNull
    @ElementCollection
    @Column(length = Integer.MAX_VALUE)
    private List<AvailabilitySlot> availabilitySlotList;

    public InterviewerAvailability(){}

    public InterviewerAvailability(Builder builder) {
        this.interviewerModel = builder.interviewerModel;
        this.availabilitySlotList = builder.availabilitySlotList;
    }

    public static class Builder {
        private Interviewer interviewerModel;
        private List<AvailabilitySlot> availabilitySlotList;

        public static Builder interviewerAvailabilityModelWith() {
            return new Builder();
        }

        public Builder withInterviewerModel(Interviewer interviewerModel) {
            this.interviewerModel = interviewerModel;

            return this;
        }

        public Builder withAvailabilitySlotList(List<AvailabilitySlot> availabilitySlotList) {
            this.availabilitySlotList = availabilitySlotList;

            return this;
        }

        public InterviewerAvailability build() {
            return new InterviewerAvailability(this);
        }
    }
}
