package service.candidate;

import app.exception.UserException;
import app.model.candidate.CandidateAvailability;
import app.model.candidate.Candidate;
import app.model.utils.AvailabilitySlot;
import app.model.utils.TimeSlot;
import app.repository.candidate.CandidateAvailabilityRepository;
import app.repository.candidate.CandidateRepository;
import app.service.candidate.CandidateServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CandidateServiceImplTests {
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private CandidateAvailabilityRepository candidateAvailabilityRepository;

    @InjectMocks
    private CandidateServiceImpl candidateServiceImpl;

    @Test
    public void createCandidateWithNameSuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        // Act
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Candidate savedCandidate = candidateServiceImpl.createCandidate(candidate);

        // Assert
        assertNotNull(savedCandidate);
        assertNull(savedCandidate.getCandidateAvailabilityModel());
        assertEquals(candidateName, savedCandidate.getName());
    }

    @Test(expected = UserException.class)
    public void createCandidateWithEmptyNameFails() {
        // Arrange
        String candidateName = "";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        // Act && Assert
        try {
            candidateServiceImpl.createCandidate(candidate);
        } catch (UserException be) {
            String exceptionMessage = "Candidate Name cannot be empty!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of candidate with empty name was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateWithNullNameFails() {
        // Arrange
        String candidateName = null;
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        // Act && Assert
        try {
            candidateServiceImpl.createCandidate(candidate);
        } catch (UserException be) {
            String exceptionMessage = "Candidate Name cannot be empty!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of candidate with null name was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateWithExistingNameFails() {
        // Arrange
        String existingCandidateName = "John Doe";

        String candidateToBeCreatedName = "John Doe";
        Candidate candidateToBeCreated = Candidate.Builder.candidateModelWith().withName(
                candidateToBeCreatedName).build();

        List<String> existingCandidates = Collections.singletonList(existingCandidateName);

        // Act && Assert
        when(candidateRepository.getAllNames()).thenReturn(existingCandidates);

        try {
            candidateServiceImpl.createCandidate(candidateToBeCreated);
        } catch (UserException be) {
            String exceptionMessage = "Name already exists!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of candidate with existing name was not thrown!");
    }

    @Test
    public void getAllCandidatesSuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();
        List<Candidate> candidatesToBeReturned = Collections.singletonList(candidate);

        // Act
        when(candidateRepository.findAll()).thenReturn(candidatesToBeReturned);

        List<Candidate> candidatesReturned = candidateServiceImpl.getAllCandidates();

        // Assert
        assertNotNull(candidatesReturned);
        assertEquals(candidatesToBeReturned.size(), candidatesReturned.size());
        assertEquals(candidatesToBeReturned, candidatesReturned);
    }

    @Test
    public void getCandidateByNameSuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));

        Optional<Candidate> candidateReturned = candidateServiceImpl.getCandidateByName(candidateName);

        // Assert
        assertNotNull(candidateReturned);
        assertEquals(candidateName, candidateReturned.get().getName());
        assertEquals(candidate, candidateReturned.get());
    }

    @Test
    public void deleteCandidateByNameSuccessfully() {
        // Arrange
        String candidateName = "John Doe";

        // Act
        candidateServiceImpl.deleteCandidateByName(candidateName);

        // Assert
        verify(candidateRepository, times(1)).deleteById(candidateName);
    }

    @Test
    public void createCandidateAvailabilityNewAvailabilitySuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(candidateAvailabilityRepository.getAvailability(candidateName)).thenReturn(null);
        when(candidateAvailabilityRepository.save(candidateAvailability)).thenReturn(candidateAvailability);

        CandidateAvailability savedCandidateAvailability = candidateServiceImpl.createCandidateAvailability(
                candidateAvailability);

        // Assert
        assertNotNull(savedCandidateAvailability);
        assertEquals(candidateName, savedCandidateAvailability.getCandidateModel().getName());
        assertEquals(availabilitySlots, savedCandidateAvailability.getAvailabilitySlotList());
    }

    @Test
    public void createCandidateAvailabilityAddToExistingAvailabilityFromDifferentDaySuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot existingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0))
                                                    .build();
        List<TimeSlot> existingTimeSlots = new ArrayList<>();
        existingTimeSlots.add(existingTimeSlot);

        AvailabilitySlot existingAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(existingTimeSlots).build();
        List<AvailabilitySlot> existingAvailabilitySlots = new ArrayList<>();
        existingAvailabilitySlots.add(existingAvailabilitySlot);

        CandidateAvailability existingCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(existingAvailabilitySlots)
                                                  .build();

        TimeSlot newTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                               .build();
        List<TimeSlot> newTimeSlots = new ArrayList<>();
        newTimeSlots.add(newTimeSlot);

        AvailabilitySlot newAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 2)).withTimeSlotList(newTimeSlots).build();
        List<AvailabilitySlot> newAvailabilitySlots = new ArrayList<>();
        newAvailabilitySlots.add(newAvailabilitySlot);

        CandidateAvailability newCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(newAvailabilitySlots)
                                                  .build();

        List<AvailabilitySlot> returnedAvailabilitySlots = new ArrayList<>();
        returnedAvailabilitySlots.addAll(existingAvailabilitySlots);
        returnedAvailabilitySlots.addAll(newAvailabilitySlots);

        CandidateAvailability returnedCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(returnedAvailabilitySlots)
                                                  .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(candidateAvailabilityRepository.getAvailability(candidateName)).thenReturn(
                existingCandidateAvailability);
        when(candidateAvailabilityRepository.save(any(CandidateAvailability.class))).thenReturn(
                returnedCandidateAvailability);

        CandidateAvailability savedCandidateAvailability = candidateServiceImpl.createCandidateAvailability(
                newCandidateAvailability);

        // Assert
        assertNotNull(savedCandidateAvailability);
        assertEquals(returnedCandidateAvailability.getAvailabilitySlotList().size(),
                     savedCandidateAvailability.getAvailabilitySlotList().size());
        assertEquals(returnedCandidateAvailability, savedCandidateAvailability);
    }

    @Test
    public void createCandidateAvailabilityAddToExistingAvailabilityFromSameDaySuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot existingTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(
                LocalTime.of(11, 0))
                                                    .build();
        List<TimeSlot> existingTimeSlots = new ArrayList<>();
        existingTimeSlots.add(existingTimeSlot);

        AvailabilitySlot existingAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(existingTimeSlots).build();
        List<AvailabilitySlot> existingAvailabilitySlots = new ArrayList<>();
        existingAvailabilitySlots.add(existingAvailabilitySlot);

        CandidateAvailability existingCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(existingAvailabilitySlots)
                                                  .build();

        TimeSlot newTimeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(13, 0)).withTo(LocalTime.of(15, 0))
                                               .build();
        List<TimeSlot> newTimeSlots = new ArrayList<>();
        newTimeSlots.add(newTimeSlot);

        AvailabilitySlot newAvailabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(newTimeSlots).build();
        List<AvailabilitySlot> newAvailabilitySlots = new ArrayList<>();
        newAvailabilitySlots.add(newAvailabilitySlot);

        CandidateAvailability newCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(newAvailabilitySlots)
                                                  .build();

        List<AvailabilitySlot> returnedAvailabilitySlots = new ArrayList<>();
        returnedAvailabilitySlots.addAll(existingAvailabilitySlots);
        returnedAvailabilitySlots.addAll(newAvailabilitySlots);

        CandidateAvailability returnedCandidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(returnedAvailabilitySlots)
                                                  .build();

        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));
        when(candidateAvailabilityRepository.getAvailability(candidateName)).thenReturn(
                existingCandidateAvailability);
        when(candidateAvailabilityRepository.save(any(CandidateAvailability.class))).thenReturn(
                returnedCandidateAvailability);

        CandidateAvailability savedCandidateAvailability = candidateServiceImpl.createCandidateAvailability(
                newCandidateAvailability);

        // Assert
        assertNotNull(savedCandidateAvailability);
        assertEquals(returnedCandidateAvailability.getAvailabilitySlotList().size(),
                     savedCandidateAvailability.getAvailabilitySlotList().size());
        assertEquals(returnedCandidateAvailability, savedCandidateAvailability);
    }

    @Test(expected = UserException.class)
    public void createCandidateAvailabilityWithNonExistingCandidateFails() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);


        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.empty());

        try {
            candidateServiceImpl.createCandidateAvailability(candidateAvailability);
        } catch (UserException be) {
            String exceptionMessage = "Candidate does not exist!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of candidate availability with non-existing candidate was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateAvailabilityWithFromAfterToFails() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(12, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);


        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));

        try {
            candidateServiceImpl.createCandidateAvailability(candidateAvailability);
        } catch (UserException be) {
            String exceptionMessage = "Start hour of slot must be before end hour of slot!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of from after to was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateAvailabilityWithFromEqualsToFails() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(11, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);


        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));

        try {
            candidateServiceImpl.createCandidateAvailability(candidateAvailability);
        } catch (UserException be) {
            String exceptionMessage = "Start hour of slot must be before end hour of slot!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of from equals to was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateAvailabilityWithInvalidFromFails() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(10, 30)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);


        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));

        try {
            candidateServiceImpl.createCandidateAvailability(candidateAvailability);
        } catch (UserException be) {
            String exceptionMessage =
                    "Availability slot must be from the beginning of the hour until the beginning of the next hour!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of invalid from was not thrown!");
    }

    @Test(expected = UserException.class)
    public void createCandidateAvailabilityWithInvalidToFails() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(10, 0)).withTo(LocalTime.of(11, 30))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);


        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act && Assert
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidate));

        try {
            candidateServiceImpl.createCandidateAvailability(candidateAvailability);
        } catch (UserException be) {
            String exceptionMessage =
                    "Availability slot must be from the beginning of the hour until the beginning of the next hour!";
            assertEquals(exceptionMessage, be.getMessage());
            throw be;
        }

        fail("Business exception of invalid to was not thrown!");
    }

    @Test
    public void getAllCandidatesAvailabilitySuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        List<CandidateAvailability> candidatesAvailabilitiesToBeReturned = Collections.singletonList(
                candidateAvailability);

        // Act
        when(candidateAvailabilityRepository.findAll()).thenReturn(candidatesAvailabilitiesToBeReturned);

        List<CandidateAvailability> candidatesAvailabilitiesReturned =
                candidateServiceImpl.getAllCandidatesAvailability();

        // Assert
        assertNotNull(candidatesAvailabilitiesReturned);
        assertEquals(candidatesAvailabilitiesToBeReturned.size(), candidatesAvailabilitiesReturned.size());
        assertEquals(candidatesAvailabilitiesToBeReturned, candidatesAvailabilitiesReturned);
    }

    @Test
    public void getCandidateAvailabilityByNameSuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();


        // Act
        when(candidateAvailabilityRepository.getAvailability(candidateName)).thenReturn(
                candidateAvailability);


        CandidateAvailability candidateAvailabilityReturned = candidateServiceImpl.getCandidateAvailabilityByName(
                candidateName);

        // Assert
        assertNotNull(candidateAvailabilityReturned);
        assertEquals(candidateAvailability, candidateAvailabilityReturned);
        assertEquals(candidateAvailability.getCandidateModel(), candidateAvailabilityReturned.getCandidateModel());
    }

    @Test
    public void deleteCandidateAvailabilityByNameSuccessfully() {
        // Arrange
        String candidateName = "John Doe";
        Candidate candidate = Candidate.Builder.candidateModelWith().withName(candidateName).build();

        TimeSlot timeSlot = TimeSlot.Builder.timeSlotWith().withFrom(LocalTime.of(9, 0)).withTo(LocalTime.of(11, 0))
                                            .build();
        List<TimeSlot> timeSlots = Collections.singletonList(timeSlot);

        AvailabilitySlot availabilitySlot = AvailabilitySlot.Builder.availabilitySlotWith().withDay(
                LocalDate.of(2014, Month.JANUARY, 1)).withTimeSlotList(timeSlots).build();
        List<AvailabilitySlot> availabilitySlots = Collections.singletonList(availabilitySlot);

        CandidateAvailability candidateAvailability =
                CandidateAvailability.Builder.candidateAvailabilityModelWith()
                                                  .withCandidateModel(candidate)
                                                  .withAvailabilitySlotList(availabilitySlots)
                                                  .build();

        // Act
        when(candidateAvailabilityRepository.getAvailability(candidateName)).thenReturn(candidateAvailability);

        candidateServiceImpl.deleteCandidateAvailabilityByName(candidateName);

        // Assert
        verify(candidateAvailabilityRepository, times(1)).deleteById(any());
    }
}
