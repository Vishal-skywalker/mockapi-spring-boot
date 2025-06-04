package com.vishal.mockapi.controllers;

import com.vishal.mockapi.entity.Path;
import com.vishal.mockapi.repository.PathRepo;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private PathRepo pathRepo;

    @RequestMapping("/{uuid}/**")
    public ResponseEntity<String> processRequest(@PathVariable("uuid") String uuid, HttpServletRequest request) {

        String fullPath = request.getRequestURI();
        String basePath = "/api/" + uuid;
        String pathName = fullPath.length() > basePath.length() ? fullPath.substring(basePath.length()) : "/";

        String method = request.getMethod(); // GET, POST, etc.

        Optional<Path> matchedPath = pathRepo.findByResourceUuidAndPathNameAndMethod(uuid, pathName, method);

        if (matchedPath.isEmpty()) {
            return ResponseEntity.status(404).body("404 or 405 - No matching mock path found.");
        }

        Path path = matchedPath.get();

        if (path.getResource().isRequireAuthentication()) {
            String apiKey = request.getHeader("X-API-Key");
            if (apiKey == null) {
                apiKey = request.getParameter("apiKey");
            }
            if (apiKey == null) {
                apiKey = request.getParameter("apikey");
            }
            if (apiKey == null) {
                apiKey = request.getParameter("api_key");
            }
            if (apiKey == null || !apiKey.equals(path.getResource().getApiKey())) {
                return ResponseEntity.status(401).body("Unauthorized: Invalid API Key");
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, path.getContentType());

        return ResponseEntity
                .status(path.getStatusCode())
                .headers(headers)
                .body(path.getResponseBody());
    }
}
