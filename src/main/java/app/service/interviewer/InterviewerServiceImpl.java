package app.service.interviewer;

import app.exception.UserException;
import app.model.interviewer.InterviewerAvailability;
import app.model.interviewer.Interviewer;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewerServiceImpl implements InterviewerService {
    private final InterviewerRepository interviewerRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @Autowired
    public InterviewerServiceImpl(InterviewerRepository interviewerRepository,
                                  InterviewerAvailabilityRepository interviewerAvailabilityRepository) {
        this.interviewerRepository = interviewerRepository;
        this.interviewerAvailabilityRepository = interviewerAvailabilityRepository;
    }

    @Override
    public Interviewer createInterviewer(Interviewer interviewerModel) {
        verifyValidityOfInterviewer(interviewerModel);

        return interviewerRepository.save(interviewerModel);
    }

    @Override
    public List<Interviewer> getAllInterviewers() {
        return interviewerRepository.findAll();
    }

    @Override
    public Optional<Interviewer> getInterviewerByName(String name) {
        return interviewerRepository.findById(name);
    }

    @Override
    public void deleteInterviewerByName(String name) {
        interviewerRepository.deleteById(name);
    }

    @Override
    public InterviewerAvailability createInterviewerAvailability(
            InterviewerAvailability interviewerAvailabilityModel) {
        verifyValidityOfInterviewerAvailability(interviewerAvailabilityModel);

        InterviewerAvailability interviewerExistingAvailability = verifyIfInterviewerHasAvailabilityCreated(
                interviewerAvailabilityModel);

        if (interviewerExistingAvailability != null) {
            addNewAvailability(interviewerExistingAvailability, interviewerAvailabilityModel);

            return interviewerAvailabilityRepository.save(interviewerExistingAvailability);
        }

        return interviewerAvailabilityRepository.save(interviewerAvailabilityModel);
    }

    @Override
    public List<InterviewerAvailability> getAllInterviewersAvailability() {
        return interviewerAvailabilityRepository.findAll();
    }

    @Override
    public InterviewerAvailability getInterviewerAvailabilityByName(String name) {
        return interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(name);
    }

    @Override
    public void deleteInterviewerAvailabilityByName(String name) {
        Long interviewerAvailabilityIdToBeDeleted =
                interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(name).getId();

        interviewerAvailabilityRepository.deleteById(interviewerAvailabilityIdToBeDeleted);
    }

    private void verifyValidityOfInterviewer(Interviewer interviewerModel) {
        verifyNameIsFilled(interviewerModel);
        verifyUniqueName(interviewerModel);
    }

    private void verifyNameIsFilled(Interviewer interviewerModel) {
        String nameOfInterviewerToBeCreated = interviewerModel.getName();

        if (nameOfInterviewerToBeCreated == null || nameOfInterviewerToBeCreated.isBlank()) {
            throw new UserException("You must provide a name!",
                                        interviewerModel.getName() != null ? interviewerModel.getName() : null);
        }
    }

    private void verifyUniqueName(Interviewer interviewerModel) {
        String nameOfInterviewerToBeCreated = interviewerModel.getName();
        List<String> existingNames = interviewerRepository.getAllNames();

        if (existingNames.contains(nameOfInterviewerToBeCreated)) {
            throw new UserException("Name already exists!", interviewerModel.getName());
        }
    }

    private void verifyValidityOfInterviewerAvailability(InterviewerAvailability interviewerAvailabilityModel) {
        verifyInterviewerExists(interviewerAvailabilityModel);
        verifyPeriodOfAvailabilityIsValid(interviewerAvailabilityModel);
    }

    private void verifyInterviewerExists(InterviewerAvailability interviewerAvailabilityModel) {
        Optional<Interviewer> interviewerModel = interviewerRepository.findById(
                interviewerAvailabilityModel.getInterviewerModel().getName());

        if (interviewerModel.isEmpty()) {
            throw new UserException("Interviewer does not exist!",
                                        interviewerAvailabilityModel.getInterviewerModel().getName());
        }
    }

    private void verifyPeriodOfAvailabilityIsValid(InterviewerAvailability interviewerAvailabilityModel) {
        List<AvailabilitySlot> availabilitySlotList = interviewerAvailabilityModel.getAvailabilitySlotList();

        for (AvailabilitySlot availabilitySlot : availabilitySlotList) {
            List<TimeSlot> timeSlotList = availabilitySlot.getTimeSlotList();

            for (TimeSlot timeSlot : timeSlotList) {
                LocalTime newTimeSlotFromTime = timeSlot.getFrom();
                LocalTime newTimeSlotToTime = timeSlot.getTo();

                if (newTimeSlotFromTime.isAfter(newTimeSlotToTime) || newTimeSlotFromTime.equals(newTimeSlotToTime)) {
                    throw new UserException("Start hour of slot must be before end hour of slot!",
                                                "From: " + newTimeSlotFromTime,
                                                "To: " + newTimeSlotToTime);
                }

                if (newTimeSlotFromTime.getMinute() != 0 || newTimeSlotToTime.getMinute() != 0) {
                    throw new UserException(
                            "Availability slot must be from the beginning of the hour until the beginning of the next "
                            + "hour!",
                            "From: " + newTimeSlotFromTime, "To: " + newTimeSlotToTime);
                }
            }
        }
    }

    private InterviewerAvailability verifyIfInterviewerHasAvailabilityCreated(
            InterviewerAvailability interviewerAvailabilityModel) {
        String interviewerName = interviewerAvailabilityModel.getInterviewerModel().getName();

        return interviewerAvailabilityRepository.getInterviewerAvailabilityByInterviewerName(interviewerName);
    }

    private void addNewAvailability(InterviewerAvailability interviewerExistingAvailabilityModel,
                                    InterviewerAvailability interviewerAvailabilityModel) {
        List<AvailabilitySlot> existingAvailabilitySlotList =
                interviewerExistingAvailabilityModel.getAvailabilitySlotList();
        List<AvailabilitySlot> newAvailabilitySlotList = interviewerAvailabilityModel.getAvailabilitySlotList();

        List<AvailabilitySlot> remainingNewAvailabilitySlotList = addNewAvailabilityToExistingDay(
                existingAvailabilitySlotList, newAvailabilitySlotList);

        if (!remainingNewAvailabilitySlotList.isEmpty()) {
            addNewAvailabilityToNewDay(existingAvailabilitySlotList, remainingNewAvailabilitySlotList);
        }
    }

    private List<AvailabilitySlot> addNewAvailabilityToExistingDay(
            List<AvailabilitySlot> existingAvailabilitySlotList, List<AvailabilitySlot> newAvailabilitySlotList) {
        List<AvailabilitySlot> addedAvailabilitiesList = new ArrayList<>();

        for (AvailabilitySlot existingAvailabilitySlot : existingAvailabilitySlotList) {
            List<TimeSlot> existingTimeSlotList = existingAvailabilitySlot.getTimeSlotList();
            LocalDate existingDay = existingAvailabilitySlot.getDay();

            for (AvailabilitySlot newAvailabilitySlot : newAvailabilitySlotList) {
                List<TimeSlot> newTimeSlotList = newAvailabilitySlot.getTimeSlotList();
                LocalDate newDay = newAvailabilitySlot.getDay();

                if (existingDay.isEqual(newDay)) {
                    existingTimeSlotList.addAll(newTimeSlotList);
                    addedAvailabilitiesList.add(newAvailabilitySlot);
                }
            }
        }

        if (!addedAvailabilitiesList.isEmpty()) {
            newAvailabilitySlotList.removeAll(addedAvailabilitiesList);
        }

        return newAvailabilitySlotList;
    }

    private void addNewAvailabilityToNewDay(List<AvailabilitySlot> existingAvailabilitySlotList,
                                            List<AvailabilitySlot> remainingNewAvailabilitySlotList) {
        existingAvailabilitySlotList.addAll(remainingNewAvailabilitySlotList);
    }
}
