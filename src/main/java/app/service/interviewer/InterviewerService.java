package app.service.interviewer;

import app.model.interviewer.InterviewerAvailability;
import app.model.interviewer.Interviewer;

import java.util.List;
import java.util.Optional;

public interface InterviewerService {
    Interviewer createInterviewer(Interviewer interviewerModel);

    List<Interviewer> getAllInterviewers();

    Optional<Interviewer> getInterviewerByName(String name);

    void deleteInterviewerByName(String name);

    InterviewerAvailability createInterviewerAvailability(
            InterviewerAvailability interviewerAvailabilityModel);

    List<InterviewerAvailability> getAllInterviewersAvailability();

    InterviewerAvailability getInterviewerAvailabilityByName(String name);

    void deleteInterviewerAvailabilityByName(String name);
}
