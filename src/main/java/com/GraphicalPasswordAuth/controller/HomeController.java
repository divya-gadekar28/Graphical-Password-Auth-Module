package com.GraphicalPasswordAuth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class that handles requests for the home and signup pages.
 * 
 * This class is part of the graphical password authentication system's web layer,
 * providing redirection to the appropriate static HTML pages.
 */
@Controller
public class HomeController {
    
    /**
     * Handles the root ("/") request and redirects to the index page.
     *
     * @return A redirect instruction to index.html
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    /**
     * Handles the "/signup" request and redirects to the signup page.
     *
     * @return A redirect instruction to signup.html
     */
    @GetMapping("/signup")
    public String signup() {
        return "redirect:/signup.html";
    }

}
