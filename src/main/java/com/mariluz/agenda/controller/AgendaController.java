package com.mariluz.agenda.controller;

import com.mariluz.agenda.model.User;
import com.mariluz.agenda.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private AgendaService service;

    @GetMapping("/me")
    public User me() {
        return service.me();
    }
}
