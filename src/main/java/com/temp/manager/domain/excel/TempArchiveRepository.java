package com.temp.manager.domain.excel;

import com.temp.manager.domain.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TempArchiveRepository extends JpaRepository<Temp, Long> {

    @Query(value = "SELECT t FROM Temp t WHERE t.dateTime BETWEEN :startDateTime AND :endDateTime")
    List<Temp> findTempsByDateTimeBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE tempdata", nativeQuery = true)
    void truncateTable();
}
