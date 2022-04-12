package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HonorRepository extends JpaRepository<Honor,Integer> {
    Optional<Honor> findByStudentNum(String studentNum);
    List<Honor> findByStudentName(String studentName);

    @Query(value = "select max(id) from Honor  ")
    Integer getMaxId();

    @Query(value = "from Honor where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Honor> findHonorListByNumName(String numName);



}
