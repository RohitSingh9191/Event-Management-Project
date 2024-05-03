package com.mirai.controllers;

import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.service.UserService;
import com.mirai.utils.ExcelExporter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
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
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<UserResponse> save(@RequestBody UserRequest userRequest) {
        UserResponse addUserResponse = userService.save(userRequest);
        return new ResponseEntity<>(addUserResponse, HttpStatus.CREATED);
    }

    @CrossOrigin
    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> userResponseList = userService.getAll();
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
