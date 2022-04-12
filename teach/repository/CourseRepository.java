package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.StuInCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Integer> {
    Optional<Course> findByCourseNum(String courseNum);

    List<Course> findByCourseName(String courseName);

    @Query(value = "select max(id) from Course  ")
    Integer getMaxId();

    @Query(value = "from Course where ?1='' or courseNum like %?1% or courseName like %?1% ")
    List<Course> findCourseListByNumName(String courseNumName);

    @Query(value = "from Course where ?1=id")
    Course findCourseById(Integer id);

    @Query(value = "SELECT sc.scores FROM stu_in_course sc WHERE course_id=?1 and student_id=?2", nativeQuery = true)
    Optional<String> findScoresByStudentCourseId(Integer courseId,Integer studentId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM stu_in_course WHERE course_id=?1 and student_id=?2", nativeQuery = true)
    void DeleteStudentCourseRelation(Integer courseId, Integer studentId);

    @Query(value = "SELECT * FROM stu_in_course WHERE course_id=?1 and student_id=?2", nativeQuery = true)
    Optional<StuInCourse> findStudentCourseRelation(Integer courseId, Integer studentId);
}