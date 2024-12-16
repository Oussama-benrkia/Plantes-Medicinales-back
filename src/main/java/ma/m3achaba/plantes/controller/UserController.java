package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.exception.ResourceNotFoundException;
import ma.m3achaba.plantes.services.imp.UserService;
import ma.m3achaba.plantes.validation.OnCreate;
import ma.m3achaba.plantes.validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String role) {

        PageResponse<UserResponse> responses;

        if (search.isEmpty() && role.isEmpty()) {
            responses = userService.findAll(page, size);
        }else if (search.isEmpty()) {
            responses = userService.findAllWithRole(page,size,role);
        } else {
            responses = userService.findAllWithSearchAndRole(page,size,search, role);
        }
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> findAllUsers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String role) {

        List<UserResponse> responses;

        if (search.isEmpty() && role.isEmpty()) {
            responses = userService.findAll();
        }else if (search.isEmpty()) {
            responses = userService.findAllWithRole(role);
        } else {
            responses = userService.findAllWithSearchAndRole(search, role);
        }
        return ResponseEntity.ok(responses);
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> saveUser(
            @Validated(OnCreate.class) @ModelAttribute UserRequest request) {

        return userService.save(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElseThrow(() -> new ResourceNotFoundException("Unable to save user."));
    }


    @PutMapping(path = "/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @ModelAttribute UserRequest request) {

        return userService.update(request, id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to update user with id: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}