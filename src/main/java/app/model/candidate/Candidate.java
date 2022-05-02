package app.model.candidate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "candidate")
@Getter
public class Candidate {

    @Id
    private String name;

    @JsonIgnore
    @OneToOne(mappedBy = "candidateModel", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private CandidateAvailability candidateAvailabilityModel;

    public Candidate(){}

    public Candidate(String name) {
        this.name = name;
    }

    public Candidate(Builder builder) {
        this.name = builder.name;
        this.candidateAvailabilityModel = builder.candidateAvailabilityModel;
    }

    public static class Builder {
        private String name;
        private CandidateAvailability candidateAvailabilityModel;

        public static Builder candidateModelWith() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;

            return this;
        }

        public Builder withCandidateAvailabilityModel(CandidateAvailability candidateAvailabilityModel) {
            this.candidateAvailabilityModel = candidateAvailabilityModel;

            return this;
        }

        public Candidate build() {
            return new Candidate(this);
        }
    }
}
