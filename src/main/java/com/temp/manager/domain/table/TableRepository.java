package com.temp.manager.domain.table;

import com.temp.manager.domain.Temp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<Temp, Long>{

}
