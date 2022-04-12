package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Innovation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InnovationRepository extends JpaRepository<Innovation,Integer>{
    Optional<Innovation> findByStudentNum(String studentNum);
    List<Innovation> findByStudentName(String studentName);

    @Query(value = "select max(id) from Innovation  ")
    Integer getMaxId();

    @Query(value = "from Innovation where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Innovation> findStudentListByNumName(String numName);
}
