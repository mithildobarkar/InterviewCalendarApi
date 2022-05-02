package app.repository.interviewer;

import app.model.interviewer.InterviewerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewerAvailabilityRepository extends JpaRepository<InterviewerAvailability, Long> {
    @Query("select ia from InterviewerAvailability ia where ia.interviewerModel.name = :name")
    InterviewerAvailability getInterviewerAvailabilityByInterviewerName(String name);
}
