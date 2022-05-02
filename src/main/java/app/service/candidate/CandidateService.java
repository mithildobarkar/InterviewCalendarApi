package app.service.candidate;

import app.model.candidate.CandidateAvailability;
import app.model.candidate.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    Candidate createCandidate(Candidate candidateModel);

    List<Candidate> getAllCandidates();

    Optional<Candidate> getCandidateByName(String name);

    void deleteCandidateByName(String name);

    CandidateAvailability createCandidateAvailability(CandidateAvailability candidateAvailabilityModel);

    List<CandidateAvailability> getAllCandidatesAvailability();

    CandidateAvailability getCandidateAvailabilityByName(String name);

    void deleteCandidateAvailabilityByName(String name);
}
