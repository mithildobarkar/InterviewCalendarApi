package app.service.interviewslots;

import app.model.interviewslots.InterviewSlotsQuery;
import app.model.interviewslots.InterviewSlotsReturn;

public interface InterviewSlotsService {
    InterviewSlotsReturn getInterviewSlots(InterviewSlotsQuery interviewSlotsQueryModel);
}
