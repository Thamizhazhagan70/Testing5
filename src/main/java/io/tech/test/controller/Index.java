package io.tech.test.controller;

import io.tech.test.model.IndexModel;
import io.tech.test.repo.IndexRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class Index {
    @Autowired
    private IndexRepo indexRepo;

    @GetMapping("/print")
    public String print() {


    	//tttttttttttr
    	//hello
    	//haiiiii
        return "Hello from the print method!";
        return "Hello from the print metggggggghod!";
    }

    @PostMapping("/create")
    public IndexModel create(@RequestBody IndexModel model) {
        return indexRepo.save(model);
    }

    @GetMapping("/all")
    public List<IndexModel> getAll() {
        return indexRepo.findAll();
    }
}
