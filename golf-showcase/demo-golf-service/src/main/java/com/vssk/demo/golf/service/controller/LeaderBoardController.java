package com.vssk.demo.golf.service.controller;


import com.vssk.demo.golf.service.util.Tournament;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaderBoardController {

    /**
     * REST endpoint to return a static Tournament details
     * @return
     */
    @GetMapping("/tournament")
    public Tournament getTournament(){
        return Tournament.getInstance();
    }
}
