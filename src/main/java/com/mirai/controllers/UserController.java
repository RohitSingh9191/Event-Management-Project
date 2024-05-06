package com.mirai.controllers;

import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.models.response.UserResponseList;
import com.mirai.service.user.UserService;
import com.mirai.utils.ExcelExporter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    /**
     * Saves user details.
     *
     * @param userRequest The request body containing user details.
     * @return ResponseEntity containing the response body with saved user details.
     */
    @PostMapping()
    public ResponseEntity<UserResponse> save(@RequestBody UserRequest userRequest) {
        log.info("Received request to save user: {}", userRequest);
        UserResponse addUserResponse = userService.save(userRequest);
        log.info("User saved successfully: {}", addUserResponse);
        return new ResponseEntity<>(addUserResponse, HttpStatus.CREATED);
    }

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<UserResponseList> getAllUser(@ModelAttribute UserFilters userFilters) {
        UserResponseList userResponseList = userService.getAllUsers(userFilters);
        return new ResponseEntity<>(userResponseList, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @CrossOrigin
    public ResponseEntity<Resource> exportToExcel() throws IOException {
        List<UserResponse> tasks = userService.getAll();
        ByteArrayInputStream excelStream = ExcelExporter.exportToExcel(tasks);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=tasks.xls");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(excelStream));
    }
}
