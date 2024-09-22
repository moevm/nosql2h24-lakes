package com.example.demo.controller;

import com.example.demo.model.Person;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {
    private PersonRepository personRepository;
    @GetMapping()
    @ResponseBody
    @Transactional
    public String addData() {
        Person person = new Person("Test Person", 23);

        personRepository.save(person);

        return "Person added: " + person.getName();
    }
}
