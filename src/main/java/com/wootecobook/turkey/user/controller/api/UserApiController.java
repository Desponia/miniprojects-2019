package com.wootecobook.turkey.user.controller.api;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.user.service.UserDeleteService;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private UserService userService;
    private UserDeleteService userDeleteService;

    public UserApiController(UserService userService, UserDeleteService userDeleteService) {
        this.userService = userService;
        this.userDeleteService = userDeleteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> show(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserResponseById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.save(userRequest);
        URI uri = linkTo(UserApiController.class).slash(userResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(userResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list(UserSession userSession) {
        return ResponseEntity.ok(userService.findAllUsersWithoutCurrentUser(userSession.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id, UserSession userSession, HttpSession httpSession) {
        userDeleteService.delete(id, userSession.getId());
        httpSession.removeAttribute(UserSession.USER_SESSION_KEY);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/search")
    public ResponseEntity<Page<UserResponse>> search(@PathVariable String name,
                                                     @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserResponse> userResponses = userService.findByName(name,pageable);
        return ResponseEntity.ok(userResponses);
    }

}