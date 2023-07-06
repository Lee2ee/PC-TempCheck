package com.temp.manager.domain.table;

import com.temp.manager.domain.Temp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    public List<Temp> getAbnormalData() {
        List<Temp> allData = tableRepository.findAll();
        return allData.stream()
                .filter(temp -> !temp.getState().equals("normal"))
                .collect(Collectors.toList());
    }

    public void deleteById(long id) {
        tableRepository.deleteById(id);
    }
}
