package com.vishal.mockapi.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.vishal.mockapi.entity.Resource;
import com.vishal.mockapi.entity.UserEntity;
import com.vishal.mockapi.entity.Path;
import com.vishal.mockapi.repository.PathRepo;
import com.vishal.mockapi.repository.ResourceRepo;
import com.vishal.mockapi.service.ApiKeyGenerator;
import com.vishal.mockapi.service.UserService;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceRepo resourceRepository;

    @Autowired
    private PathRepo pathRepo;

    @Autowired
    private UserService userService;

    @GetMapping("/{uuid}")
    public String getResourceByUuid(@PathVariable String uuid, Model model) {
        UserEntity currentUser = userService.getCurrentUser();
        Resource resource = resourceRepository.findByOwnerIdAndUUIDWithPaths(currentUser.getId(), uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        model.addAttribute("resource", resource);
        return "resource-details";
    }

    @GetMapping("/new-api")
    public String createNewResource(Model model) {
        UserEntity currentUser = userService.getCurrentUser();
        Resource newResource = new Resource();
        newResource.setOwner(currentUser);
        newResource.setRequireAuthentication(false);
        newResource.setApiKey(ApiKeyGenerator.generateApiKey());
        newResource.setUuid(UUID.randomUUID().toString());
        newResource.setName("New MockAPI Resource");
        newResource.setDescription("This is a new mock API resource created by " + currentUser.getName());
        resourceRepository.save(newResource);
        return "redirect:/resource/" + newResource.getUuid();
    }

    @PostMapping("/{uuid}")
    public String updateResource(@PathVariable String uuid, Resource updatedResource, Model model) {
        UserEntity currentUser = userService.getCurrentUser();
        List<Resource> resources = resourceRepository.findByOwnerIdAndUuid(currentUser.getId(), uuid);
        if (resources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        resources.stream().forEach(r -> {
            r.setName(updatedResource.getName());
            r.setDescription(updatedResource.getDescription());
            r.setRequireAuthentication(updatedResource.isRequireAuthentication());
            r.setApiKey(updatedResource.getApiKey());
        });
        resourceRepository.saveAll(resources);
        return "redirect:/resource/" + uuid;
    }

    @PostMapping("/{uuid}/delete")
    public String deleteResource(@PathVariable String uuid) {
        UserEntity currentUser = userService.getCurrentUser();
        List<Resource> resources = resourceRepository.findByOwnerIdAndUuid(currentUser.getId(), uuid);
        if (resources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        resourceRepository.deleteAll(resources);
        return "redirect:/";
    }

    @PostMapping("/{uuid}/path/{id}/delete")
    public String deletePath(@PathVariable String uuid, @PathVariable Long id) {
        Path path = pathRepo.findByResourceUuidAndId(uuid, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Path not found"));
        pathRepo.delete(path);
        return "redirect:/resource/" + uuid;
    }

    @PostMapping("/{uuid}/path/{id}")
    public String updatePath(@PathVariable String uuid, @PathVariable Long id, Path updatedPath) {
        Path path = pathRepo.findByResourceUuidAndId(uuid, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Path not found"));
        path.setContentType(updatedPath.getContentType());
        path.setMethod(updatedPath.getMethod());
        path.setPathName(updatedPath.getPathName().startsWith("/") ? updatedPath.getPathName()
                : "/" + updatedPath.getPathName());
        path.setResponseBody(updatedPath.getResponseBody());
        path.setStatusCode(updatedPath.getStatusCode() < 100 || updatedPath.getStatusCode() > 599 ? 200
                : updatedPath.getStatusCode());
        pathRepo.save(path);
        return "redirect:/resource/" + uuid + "#path-" + id;
    }

    @PostMapping("/{uuid}/path/new")
    public String newPath(@PathVariable String uuid) {
        UserEntity currentUser = userService.getCurrentUser();
        List<Resource> resources = resourceRepository.findByOwnerIdAndUuid(currentUser.getId(), uuid);
        if (resources.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        Path path = new Path();
        path.setResource(resources.get(0));
        path.setContentType("application/json");
        path.setMethod("GET");
        path.setPathName("/new-path");
        path.setResponseBody("{}");
        path.setStatusCode(200);
        pathRepo.save(path);
        return "redirect:/resource/" + uuid + "#path-" + path.getId();
    }
}
