package app.service.interviewslots;

import app.exception.UserException;
import app.model.candidate.CandidateAvailability;
import app.model.candidate.Candidate;
import app.model.interviewer.InterviewerAvailability;
import app.model.interviewer.Interviewer;
import app.model.interviewslots.InterviewSlotsQuery;
import app.model.interviewslots.InterviewSlotsReturn;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import app.repository.interviewer.InterviewerAvailabilityRepository;
import app.repository.interviewer.InterviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class InterviewSlotsServiceImpl implements InterviewSlotsService {
    private final CandidateRepository candidateRepository;
    private final CandidateAvailabilityRepository candidateAvailabilityRepository;
    private final InterviewerRepository interviewerRepository;
    private final InterviewerAvailabilityRepository interviewerAvailabilityRepository;

    @Autowired
    public InterviewSlotsServiceImpl(CandidateRepository candidateRepository,
                                     CandidateAvailabilityRepository candidateAvailabilityRepository,
                                     InterviewerRepository interviewerRepository,
                                     InterviewerAvailabilityRepository interviewerAvailabilityRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateAvailabilityRepository = candidateAvailabilityRepository;
        this.interviewerRepository = interviewerRepository;
        this.interviewerAvailabilityRepository = interviewerAvailabilityRepository;
    }

    @Override
    public InterviewSlotsReturn getInterviewSlots(InterviewSlotsQuery interviewSlotsQueryModel) {
        verifyCandidateAndInterviewersExist(interviewSlotsQueryModel);

        String candidateName = interviewSlotsQueryModel.getCandidateName();
        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();
        List<AvailabilitySlot> interviewAvailabilitySlots = getInterviewAvailabilitySlots(interviewSlotsQueryModel);

        InterviewSlotsReturn interviewSlotsReturnModel =
                InterviewSlotsReturn.Builder.interviewSlotsReturnModelWith()
                                                 .withCandidateName(candidateName)
                                                 .withInterviewerNameList(interviewersNames)
                                                 .withInterviewAvailabilitySlotList(interviewAvailabilitySlots)
                                                 .build();

        return interviewSlotsReturnModel;
    }

    private void verifyCandidateAndInterviewersExist(InterviewSlotsQuery interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidateName();
        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();

        verifyCandidateExists(candidateName);
        verifyInterviewersExist(interviewersNames);
    }

    private void verifyCandidateExists(String candidateName) {
        Optional<Candidate> existingCandidate = candidateRepository.findById(candidateName);

        if (existingCandidate.isEmpty()) {
            throw new UserException("Candidate does not exist!", candidateName);
        }
    }

    private void verifyInterviewersExist(List<String> interviewersNames) {
        for (String interviewerName : interviewersNames) {
            Optional<Interviewer> existingInterviewer = interviewerRepository.findById(interviewerName);

            if (existingInterviewer.isEmpty()) {
                throw new UserException("Interviewer does not exist!", interviewerName);
            }
        }
    }

    private List<AvailabilitySlot> getInterviewAvailabilitySlots(InterviewSlotsQuery interviewSlotsQueryModel) {
        String candidateName = interviewSlotsQueryModel.getCandidateName();
        CandidateAvailability candidateAvailability = getCandidateAvailability(candidateName);

        List<String> interviewersNames = interviewSlotsQueryModel.getInterviewersNames();
        List<InterviewerAvailability> interviewersAvailabilities = new ArrayList<>();

        for (String interviewerName : interviewersNames) {
            InterviewerAvailability interviewerAvailability = getInterviewerAvailability(interviewerName);

            interviewersAvailabilities.add(interviewerAvailability);
        }

        Set<LocalDate> candidateAndInterviewersAvailabilitiesCommonDays =
                getCandidateAndInterviewersAvailabilitiesCommonDays(
                        candidateAvailability,
                        interviewersAvailabilities);

        List<AvailabilitySlot> interviewAvailabilitySlots = getCommonAvailabilitySlots(
                candidateAndInterviewersAvailabilitiesCommonDays,
                candidateAvailability,
                interviewersAvailabilities);

        return interviewAvailabilitySlots;
    }


    private CandidateAvailability getCandidateAvailability(String candidateName) {
        CandidateAvailability candidateAvailability =
                candidateAvailabilityRepository.getAvailability(candidateName);

        if (candidateAvailability == null) {
            throw new UserException("Candidate has no availability defined!", candidateName);
        }

        return candidateAvailability;
    }

    private InterviewerAvailability getInterviewerAvailability(String interviewerName) {
        InterviewerAvailability interviewerAvailability = interviewerAvailabilityRepository
                .getInterviewerAvailabilityByInterviewerName(interviewerName);

        if (interviewerAvailability == null) {
            throw new UserException("Interviewer has no availability defined!", interviewerName);
        }

        return interviewerAvailability;
    }

    private Set<LocalDate> getCandidateAndInterviewersAvailabilitiesCommonDays(
            CandidateAvailability candidateAvailability,
            List<InterviewerAvailability> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();

        Set<LocalDate> candidateAndInterviewersAvailabilitiesCommonDays = new HashSet<>();

        for (AvailabilitySlot candidateAvailabilitySlot : candidateAvailabilitySlots) {
            LocalDate candidateAvailabilityDay = candidateAvailabilitySlot.getDay();

            for (InterviewerAvailability interviewerAvailability : interviewersAvailabilities) {
                List<AvailabilitySlot> interviewerAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

                for (AvailabilitySlot interviewerAvailabilitySlot : interviewerAvailabilitySlots) {
                    LocalDate interviewerAvailabilityDay = interviewerAvailabilitySlot.getDay();

                    if (candidateAvailabilityDay.compareTo(interviewerAvailabilityDay) == 0) {
                        candidateAndInterviewersAvailabilitiesCommonDays.add(candidateAvailabilityDay);
                    }
                }
            }
        }

        return candidateAndInterviewersAvailabilitiesCommonDays;
    }

    private List<AvailabilitySlot> getCommonAvailabilitySlots(Set<LocalDate> commonDays,
                                                              CandidateAvailability candidateAvailability,
                                                              List<InterviewerAvailability> interviewersAvailabilities) {
        List<AvailabilitySlot> candidateAvailabilitySlots = candidateAvailability.getAvailabilitySlotList();
        List<AvailabilitySlot> interviewAvailabilitySlots = getAvailabilitySlotsOfCommonDays(commonDays,
                                                                                             candidateAvailabilitySlots);

        for (InterviewerAvailability interviewerAvailability : interviewersAvailabilities) {
            List<AvailabilitySlot> interviewerAllAvailabilitySlots = interviewerAvailability.getAvailabilitySlotList();

            List<AvailabilitySlot> interviewerCommonAvailabilitySlots = getAvailabilitySlotsOfCommonDays(
                    commonDays, interviewerAllAvailabilitySlots);

            interviewAvailabilitySlots = getOverlappingAvailabilitySlots(interviewAvailabilitySlots,
                                                                         interviewerCommonAvailabilitySlots);
        }

        return interviewAvailabilitySlots;
    }

    private List<AvailabilitySlot> getAvailabilitySlotsOfCommonDays(Set<LocalDate> commonDays,
                                                                    List<AvailabilitySlot> availabilitySlots) {
        List<AvailabilitySlot> availabilitySlotsOfCommonDays = new ArrayList<>();

        for (AvailabilitySlot availabilitySlot : availabilitySlots) {
            for (LocalDate localDate : commonDays) {
                if (availabilitySlot.getDay().compareTo(localDate) == 0) {
                    availabilitySlotsOfCommonDays.add(availabilitySlot);
                }
            }
        }

        return availabilitySlotsOfCommonDays;
    }

    private List<AvailabilitySlot> getOverlappingAvailabilitySlots(
            List<AvailabilitySlot> candidateCommonAvailabilitySlots,
            List<AvailabilitySlot> interviewerCommonAvailabilitySlots) {
        List<AvailabilitySlot> overlappingAvailabilitySlots = calculateOverlappingAvailabilitySlots(
                candidateCommonAvailabilitySlots, interviewerCommonAvailabilitySlots);

        return overlappingAvailabilitySlots;
    }

    private List<AvailabilitySlot> calculateOverlappingAvailabilitySlots(
            List<AvailabilitySlot> firstAvailabilitySlots,
            List<AvailabilitySlot> secondAvailabilitySlots) {
        List<AvailabilitySlot> overlappingAvailabilitySlots = new ArrayList<>();

        for (AvailabilitySlot firstAvailabilitySlot : firstAvailabilitySlots) {
            for (AvailabilitySlot secondAvailabilitySlot : secondAvailabilitySlots) {
                LocalDate firstAvailabilitySlotDay = firstAvailabilitySlot.getDay();
                LocalDate secondAvailabilitySlotDay = secondAvailabilitySlot.getDay();

                if (firstAvailabilitySlotDay.compareTo(secondAvailabilitySlotDay) == 0) {
                    List<TimeSlot> firstAvailabilityTimeSlots = firstAvailabilitySlot.getTimeSlotList();
                    List<TimeSlot> secondAvailabilityTimeSlots = secondAvailabilitySlot.getTimeSlotList();

                    List<TimeSlot> overlappingTimeSlots = new ArrayList<>();

                    for (TimeSlot firstTimeSlot : firstAvailabilityTimeSlots) {
                        for (TimeSlot secondTimeSlot : secondAvailabilityTimeSlots) {
                            LocalTime firstTimeSlotFrom = firstTimeSlot.getFrom();
                            LocalTime firstTimeSlotTo = firstTimeSlot.getTo();
                            LocalTime secondTimeSlotFrom = secondTimeSlot.getFrom();
                            LocalTime secondTimeSlotTo = secondTimeSlot.getTo();

                            TimeSlot overlappingTimeSlot = getOverlappingTimeSlot(firstTimeSlotFrom, firstTimeSlotTo,
                                                                                  secondTimeSlotFrom,
                                                                                  secondTimeSlotTo);

                            if (overlappingTimeSlot.getFrom() != null && overlappingTimeSlot.getTo() != null) {
                                overlappingTimeSlots.add(overlappingTimeSlot);
                            }
                        }
                    }

                    if (!overlappingTimeSlots.isEmpty()) {
                        LocalDate availabilitySlotDay = secondAvailabilitySlot.getDay();

                        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith()
                                                                                    .withDay(availabilitySlotDay)
                                                                                    .withTimeSlotList(
                                                                                            overlappingTimeSlots)
                                                                                    .build();

                        overlappingAvailabilitySlots.add(availabilitySlot);
                    }
                }
            }
        }

        return overlappingAvailabilitySlots;
    }

    private TimeSlot getOverlappingTimeSlot(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
                                            LocalTime interviewerTo) {
        TimeSlot overlappingTimeSlot = new TimeSlot();
        LocalTime overlapTimeSlotFrom;
        LocalTime overlapTimeSlotTo;

        if (candidateFrom.isBefore(interviewerTo) && interviewerFrom.isBefore(candidateTo)) {
            if (candidateFrom.isBefore(interviewerFrom)) {
                overlapTimeSlotFrom = interviewerFrom;
            } else {
                overlapTimeSlotFrom = candidateFrom;
            }

            if (candidateTo.isBefore(interviewerTo)) {
                overlapTimeSlotTo = candidateTo;
            } else {
                overlapTimeSlotTo = interviewerTo;
            }

            overlappingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(overlapTimeSlotFrom).withTo(
                    overlapTimeSlotTo).build();
        }

        return overlappingTimeSlot;
    }
}
