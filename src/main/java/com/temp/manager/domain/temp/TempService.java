package com.temp.manager.domain.temp;

import com.temp.manager.domain.Temp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TempService {

    @Autowired
    private TempRepository tempRepository;

    public List<Temp> getAllTemps() {
        List<Temp> tempList = new ArrayList<>();
        tempRepository.findAll().forEach(tempList::add);
        return tempList;
    }
}
