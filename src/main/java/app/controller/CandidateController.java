package app.controller;

import app.model.candidate.CandidateAvailability;
import app.model.candidate.Candidate;
import app.service.candidate.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/candidates")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate createCandidate(@Valid @RequestBody Candidate candidateModel) {
        return candidateService.createCandidate(candidateModel);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Candidate> getAllCandidates() {
        return candidateService.getAllCandidates();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Candidate> getCandidateByName(@PathVariable String name) {
        return candidateService.getCandidateByName(name);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidateByName(@PathVariable String name) {
        candidateService.deleteCandidateByName(name);
    }

    @PostMapping("/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateAvailability createCandidateAvailability(
            @Valid @RequestBody CandidateAvailability candidateAvailabilityModel) {
        return candidateService.createCandidateAvailability(candidateAvailabilityModel);
    }

    @GetMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    public List<CandidateAvailability> getAllCandidatesAvailability() {
        return candidateService.getAllCandidatesAvailability();
    }

    @GetMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CandidateAvailability getCandidateAvailabilityByName(@PathVariable String name) {
        return candidateService.getCandidateAvailabilityByName(name);
    }

    @DeleteMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCandidateAvailabilityByName(@PathVariable String name) {
        candidateService.deleteCandidateAvailabilityByName(name);
    }
}
