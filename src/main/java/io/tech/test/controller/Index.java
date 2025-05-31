package io.tech.test.controller;

import io.tech.test.model.IndexModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class Index {
  

    @GetMapping("/print")
    public String print() {

    	//tttttttttttr
    	//hello


    	//tttttttttttr
    	//hello
    	//haiiiii	//tttttttttttr
    	//hello
    	//haiiiii
    	//tttttttttttr
    	//hello

        return "Hello from the print method!";
    }
}
