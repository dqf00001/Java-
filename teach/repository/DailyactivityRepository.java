package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.DailyActivity;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DailyactivityRepository extends JpaRepository<DailyActivity,Integer> {

    Optional<Student> findByStudentNum(String studentNum);
    List<Student> findByStudentName(String studentName);

    @Query(value = "select max(id) from DailyActivity  ")
    Integer getMaxId();

    @Query(value = "from DailyActivity where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<DailyActivity> findStudentListByNumName(String numName);

}