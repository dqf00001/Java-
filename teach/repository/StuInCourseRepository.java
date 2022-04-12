package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.StuInCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StuInCourseRepository extends JpaRepository<StuInCourse,Integer> {

    @Query(value = "SELECT * FROM stu_in_course WHERE course_id=?1 and student_id=?2", nativeQuery = true)
    Optional<StuInCourse> findStudentCourseRelation(Integer courseId, Integer studentId);

}
