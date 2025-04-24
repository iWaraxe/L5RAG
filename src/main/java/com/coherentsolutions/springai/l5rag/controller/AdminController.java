package com.coherentsolutions.springai.l5rag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/reset-vector-store")
    public String resetVectorStore() {
        jdbcTemplate.execute("DELETE FROM vector_store");
        return "Vector store has been cleared.";
    }
}
