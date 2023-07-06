package com.temp.manager.domain.temp;

import com.temp.manager.domain.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TempRepository extends JpaRepository<Temp, Long> {
    List<Temp> findTop12ByIpOrderByDateTimeDesc(String ip);

    @Modifying
    @Query("SELECT DISTINCT t.ip FROM Temp t")
    List<String> findAllIps();

    @Transactional
    @Modifying
    @Query("UPDATE Temp t SET t.state = :state WHERE t.id = :id")
    void updateNormalStateById(@Param("id") Long id, @Param("state") String state);

}