package app.controller;

import app.model.interviewslots.InterviewSlotsQuery;
import app.model.interviewslots.InterviewSlotsReturn;
import app.service.interviewslots.InterviewSlotsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/interview-slots")
public class InterviewSlotsController {
    @Autowired
    private InterviewSlotsService interviewSlotsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public InterviewSlotsReturn getInterviewSlots(
            @Valid @RequestBody InterviewSlotsQuery interviewSlotsQueryModel) {
        return interviewSlotsService.getInterviewSlots(interviewSlotsQueryModel);
    }
}
