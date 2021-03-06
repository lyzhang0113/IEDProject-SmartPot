package com.doby2333.IED.controller;

import com.doby2333.IED.service.DashService;
import com.doby2333.IED.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DashService dashService;

    @RequestMapping(value = {"/", "/hello", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Map<String, Object> model, HttpSession session) {
        if (session.getAttribute("id") != null)
            model.put("msg", "Dashboard");
        else model.put("msg", "Login/Register");
        return "index";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model, HttpSession session) {
        if (session.getAttribute("id") != null)
            return "redirect:/dashboard";
        else return "login";
    }

    @PostMapping("/login")
    public String validateLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                Map<String, Object> model, HttpSession session) {
        if (userService.validateUser(username, password)) {
            session.setAttribute("id", userService.getID(username, password).toString());
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        } else {
            model.put("msg", "Incorrect Credential!");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(Map<String, Object> model, HttpSession session) {
        session.setAttribute("id", null);
        session.setAttribute("username", null);
        model.put("msg", "You have been successfully logged out!");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Map<String, Object> model) {
        return "register";
    }

    @PostMapping("/register")
    public String validateRegistration(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        Map<String, Object> model, HttpSession session) {
        boolean validRegistration = userService.register(username, password);
        if (validRegistration) {
            session.setAttribute("id", userService.getID(username, password).toString());
            session.setAttribute("username", username);
            return "redirect:/login";
        } else {
            model.put("msg", "Registration Failed! Duplicate Username!");
            return "register";
        }
    }

    @GetMapping("/account/setting")
    public String setting(Model model, HttpSession session) {
        // if not logged in, kick user out
        if (session.getAttribute("id") == null) {
            model.addAttribute("msg", "Please Login First Before Accessing This Content!");
            return "redirect:/login";
        }
        Long uid = Long.parseLong((String) session.getAttribute("id"));
        model.addAttribute("pot_count", dashService.countPots(uid));
        return "account_setting";
    }

    @PostMapping("/account/setting")
    public String changePassword(Model model, HttpSession session, @RequestParam("curr") String pass, @RequestParam("new") String new_pass) {
        // if not logged in, kick user out
        if (session.getAttribute("id") == null) {
            model.addAttribute("msg", "Please Login First Before Accessing This Content!");
            return "redirect:/login";
        }
        Long uid = Long.parseLong((String) session.getAttribute("id"));
        model.addAttribute("pot_count", dashService.countPots(uid));
        if (userService.changePassword(uid, pass, new_pass)) {
            model.addAttribute("msg", "Password has been changed successfully!");
        } else {
            model.addAttribute("warn", "Failed! Current Password you entered is incorrect!");
        }
        return "account_setting";
    }
}
