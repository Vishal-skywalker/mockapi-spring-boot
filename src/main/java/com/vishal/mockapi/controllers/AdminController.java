package com.vishal.mockapi.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vishal.mockapi.entity.UserEntity;
import com.vishal.mockapi.repository.UserRepo;

@Controller
@RequestMapping("/admin")
public class AdminController {

    static final Map<String, Sort> SORT_OPTIONS = Map.of(
            "asc", Sort.by(Sort.Direction.ASC, "id"),
            "desc", Sort.by(Sort.Direction.DESC, "id"));

    @Autowired
    UserRepo userRepo;

    @GetMapping(value = { "", "/" })
    public String admin(Model model, @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "asc") String sort) {

        Long totalUsers = userRepo.count();
        model.addAttribute("totalUsers", totalUsers);
        Pageable pageable = PageRequest.of(page, pageSize, SORT_OPTIONS.getOrDefault(sort, SORT_OPTIONS.get("asc")));
        Page<UserEntity> usersPage = userRepo.findAll(pageable);
        model.addAttribute("page", usersPage);
        return "admin";
    }

}
