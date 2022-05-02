package app.repository.interviewer;

import app.model.interviewer.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, String> {
    @Query("select i.name from Interviewer i")
    List<String> getAllNames();
}
