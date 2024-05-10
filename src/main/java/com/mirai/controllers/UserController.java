package com.mirai.controllers;

import com.google.zxing.WriterException;
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
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/save")
    public ResponseEntity<UserResponse> save(@RequestBody UserRequest userRequest) {
        log.info("Received request to save user: {}", userRequest);
        UserResponse addUserResponse = userService.save(userRequest);
        log.info("User saved successfully: {}", addUserResponse);
        return new ResponseEntity<>(addUserResponse, HttpStatus.CREATED);
    }

    /**
     * Endpoint for retrieving all users based on specified filters.
     *
     * @param userFilters The filters to apply for retrieving users.
     * @return ResponseEntity containing the list of users matching the filters.
     */
    @GetMapping()
    public ResponseEntity<UserResponseList> getAllUser(@ModelAttribute UserFilters userFilters) {
        log.info("Fetching all users with filters: '{}'", userFilters);
        UserResponseList userResponseList = userService.getAllUsers(userFilters);
        log.info("Users matching the filters: '{}'", userFilters);
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

    /**
     * Confirm user with the given ID.
     *
     * @param id The ID of the user to confirm.
     * @return ResponseEntity containing the confirmation response.
     * @throws IOException    If an I/O error occurs while confirming the user.
     * @throws WriterException If an error occurs while generating the QR code.
     */
    @PostMapping("/{id}/{status}")
    public ResponseEntity userConfirm(@PathVariable("id") Integer id, @PathVariable("status") String status)
            throws IOException, WriterException {
        log.info("Confirming user with ID: {}", id);
        String response = userService.confirmUser(id, status);
        log.info("User with ID {} confirmed successfully.", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves the profile of a user by their ID.
     *
     * @param id The ID of the user whose profile is to be retrieved.
     * @return ResponseEntity containing the user profile.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> userProfile(@PathVariable("id") Integer id) {
        log.info("Fetching profile for user with ID: {}", id);
        UserResponse response = userService.getUserProfile(id);
        log.info("Profile fetched successfully for user with ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Handles the user check-in process.
     *
     * @param id The ID of the user to check in.
     * @return ResponseEntity containing the UserResponse and HttpStatus.CREATED.
     */
    @PostMapping("/checkin/{id}")
    public ResponseEntity<UserResponse> userCheckin(@PathVariable("id") Integer id) {
        log.info("Received request to check in user with ID: {}", id);
        UserResponse userResponse = userService.userCheckin(id);
        log.info("User with ID {} checked in successfully", id);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/image/{id}")
    public ResponseEntity<UserResponse> addUserImage(
            @PathVariable("id") Integer id, @RequestParam("image") MultipartFile image) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
