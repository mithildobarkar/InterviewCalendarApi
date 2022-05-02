package app.controller;

import app.model.interviewer.InterviewerAvailability;
import app.model.interviewer.Interviewer;
import app.service.interviewer.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/interviewers")
public class InterviewerController {
    @Autowired
    private InterviewerService interviewerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Interviewer createInterviewer(@Valid @RequestBody Interviewer interviewerModel) {
        return interviewerService.createInterviewer(interviewerModel);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Interviewer> getAllInterviewers() {
        return interviewerService.getAllInterviewers();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Interviewer> getInterviewerByName(@PathVariable String name) {
        return interviewerService.getInterviewerByName(name);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterviewerByName(@PathVariable String name) {
        interviewerService.deleteInterviewerByName(name);
    }

    @PostMapping("/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewerAvailability createInterviewerAvailability(
            @Valid @RequestBody InterviewerAvailability interviewerAvailabilityModel) {
        return interviewerService.createInterviewerAvailability(interviewerAvailabilityModel);
    }

    @GetMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    public List<InterviewerAvailability> getAllInterviewersAvailability() {
        return interviewerService.getAllInterviewersAvailability();
    }

    @GetMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.OK)
    public InterviewerAvailability getCandidateAvailabilityByName(@PathVariable String name) {
        return interviewerService.getInterviewerAvailabilityByName(name);
    }

    @DeleteMapping("/availability/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterviewerAvailabilityByName(@PathVariable String name) {
        interviewerService.deleteInterviewerAvailabilityByName(name);
    }
}
