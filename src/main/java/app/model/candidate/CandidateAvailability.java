package app.model.candidate;

import app.model.utils.AvailabilitySlot;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "candidate_availability")
@Getter
public class CandidateAvailability {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JsonProperty("candidateName")
    @JoinColumn(name = "name", nullable = false)
    private Candidate candidateModel;

    @NotNull
    @ElementCollection
    @Column(length = Integer.MAX_VALUE)
    private List<AvailabilitySlot> availabilitySlotList;

    public CandidateAvailability(){}

    public CandidateAvailability(Builder builder) {
        this.candidateModel = builder.candidateModel;
        this.availabilitySlotList = builder.availabilitySlotList;
    }

    public static class Builder {
        private Candidate candidateModel;
        private List<AvailabilitySlot> availabilitySlotList;

        public static Builder candidateAvailabilityModelWith() {
            return new Builder();
        }

        public Builder withCandidateModel(Candidate candidateModel) {
            this.candidateModel = candidateModel;

            return this;
        }

        public Builder withAvailabilitySlotList(List<AvailabilitySlot> availabilitySlotList) {
            this.availabilitySlotList = availabilitySlotList;

            return this;
        }

        public CandidateAvailability build() {
            return new CandidateAvailability(this);
        }
    }
}
