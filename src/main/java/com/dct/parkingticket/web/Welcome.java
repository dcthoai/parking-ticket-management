package com.dct.parkingticket.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Welcome {

    private static final Logger log = LoggerFactory.getLogger(Welcome.class);

    @GetMapping("/")
    public String welcome() {
        log.info("Welcome to homepage!!!");
        return "<h1 style=\"width: 450px; margin: 0 auto; margin-top: 45vh;\">Welcome to Parking Tickets</h1>";
    }
}
