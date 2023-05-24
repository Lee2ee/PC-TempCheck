package com.temp.manager.domain.temp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempRepository extends JpaRepository<Temp, Long> {
    List<Temp> findTop12ByIpOrderByDateTimeDesc(String ip);

}