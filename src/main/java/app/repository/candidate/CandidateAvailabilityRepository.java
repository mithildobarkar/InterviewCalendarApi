package app.repository.candidate;

import app.model.candidate.CandidateAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateAvailabilityRepository extends JpaRepository<CandidateAvailability, Long> {
    @Query("select ca from CandidateAvailability ca where ca.candidateModel.name = :name")
    CandidateAvailability getAvailability(String name);
}
