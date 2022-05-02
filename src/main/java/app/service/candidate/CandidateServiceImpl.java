package app.service.candidate;

import app.exception.UserException;
import app.model.candidate.CandidateAvailability;
import app.model.candidate.Candidate;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CandidateAvailabilityRepository candidateAvailabilityRepository;

    public CandidateServiceImpl(){}

    @Override
    public Candidate createCandidate(Candidate candidateModel) {
        isValid(candidateModel);
        return candidateRepository.save(candidateModel);
    }

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Optional<Candidate> getCandidateByName(String name) {
        return candidateRepository.findById(name);
    }

    @Override
    public void deleteCandidateByName(String name) {
        candidateRepository.deleteById(name);
    }

    @Override
    public CandidateAvailability createCandidateAvailability(
            CandidateAvailability candidateAvailabilityModel) {
        verifyValidityOfCandidateAvailability(candidateAvailabilityModel);

        CandidateAvailability existingCandidate = alreadyExists(
                candidateAvailabilityModel);

        if (existingCandidate != null) {
            addNewAvailability(existingCandidate, candidateAvailabilityModel);

            return candidateAvailabilityRepository.save(existingCandidate);
        }

        return candidateAvailabilityRepository.save(candidateAvailabilityModel);
    }

    @Override
    public List<CandidateAvailability> getAllCandidatesAvailability() {
        return candidateAvailabilityRepository.findAll();
    }

    @Override
    public CandidateAvailability getCandidateAvailabilityByName(String name) {
        return candidateAvailabilityRepository.getAvailability(name);
    }

    @Override
    public void deleteCandidateAvailabilityByName(String name) {
        CandidateAvailability availability = candidateAvailabilityRepository.getAvailability(name);
        if(availability == null) {
            throw new UserException("Candidate does not exist!", name);
        }
        candidateAvailabilityRepository.deleteById(availability.getId());
    }

    private void isValid(Candidate candidateModel) {
        isNameBlank(candidateModel);
        isNameUnique(candidateModel);
    }

    private void isNameBlank(Candidate candidateModel) {
        if (candidateModel.getName() == null || candidateModel.getName().isBlank()) {
            throw new UserException("Candidate Name cannot be empty!");
        }
    }

    private void isNameUnique(Candidate candidateModel) {
        String nameOfCandidateToBeCreated = candidateModel.getName();
        List<String> existingNames = candidateRepository.getAllNames();

        if (existingNames.contains(nameOfCandidateToBeCreated)) {
            throw new UserException("Name already exists!", candidateModel.getName());
        }
    }

    private void verifyValidityOfCandidateAvailability(CandidateAvailability candidateAvailabilityModel) {
        verifyCandidateExists(candidateAvailabilityModel);
        validatePeriod(candidateAvailabilityModel);
    }

    private void verifyCandidateExists(CandidateAvailability candidateAvailabilityModel) {
        Optional<Candidate> candidateModel = candidateRepository.findById(
                candidateAvailabilityModel.getCandidateModel().getName());

        if (candidateModel.isEmpty()) {
            throw new UserException("Candidate does not exist!",
                                        candidateAvailabilityModel.getCandidateModel().getName());
        }
    }

    private void validatePeriod(CandidateAvailability candidateAvailabilityModel) {
        List<AvailabilitySlot> availabilitySlotList = candidateAvailabilityModel.getAvailabilitySlotList();

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
                            "Availability slot must be from the beginning of the hour until the beginning of the next"
                            + " hour!",
                            "From: " + newTimeSlotFromTime, "To: " + newTimeSlotToTime);
                }
            }
        }
    }

    private CandidateAvailability alreadyExists(
            CandidateAvailability candidateAvailabilityModel) {
        String candidateName = candidateAvailabilityModel.getCandidateModel().getName();

        return candidateAvailabilityRepository.getAvailability(candidateName);
    }

    private void addNewAvailability(CandidateAvailability candidateExistingAvailabilityModel,
                                    CandidateAvailability candidateAvailabilityModel) {
        List<AvailabilitySlot> existingAvailabilitySlotList =
                candidateExistingAvailabilityModel.getAvailabilitySlotList();
        List<AvailabilitySlot> newAvailabilitySlotList = candidateAvailabilityModel.getAvailabilitySlotList();

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
