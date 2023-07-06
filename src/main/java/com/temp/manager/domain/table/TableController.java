package com.temp.manager.domain.table;

import com.temp.manager.domain.Temp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RequestMapping("/tempsTable")
public class TableController {
    @Autowired
    private TableService tableService;
    private final Logger logger = LoggerFactory.getLogger(TableController.class);
//    @GetMapping("/temps")
    @RequestMapping("/")
    public String getAllTemps(Model model) {
        model.addAttribute("temps", tableService.getAbnormalData());
        return "index";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTempById(@PathVariable long id) {
        try {
            tableService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}