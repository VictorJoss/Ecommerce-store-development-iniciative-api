package com.example.ecomerce.api.controller.auth;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.LoginResponse;
import com.example.ecomerce.api.model.PasswordResetBody;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.EmailFailureException;
import com.example.ecomerce.exception.EmailNotFoundException;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.exception.UserNotVerifiedException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for handling authentication requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    /** The user service. */
    private UserService userService;

    /**
     * Spring injected constructor.
     * @param userService
     */
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Post Mapping to handle registering users.
     * @param registrationBody The registration information.
     * @return Response to front end.
     */
    @Operation(summary = "Register a new user", description = "Registers a new user with the system.")
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try{
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        }catch (UserAlreadyExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException ex) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Post Mapping to handle user logins to provide authentication token.
     * @param loginBody The login information.
     * @return The authentication token if successful.
     */
    @Operation(summary = "Login a user", description = "Logs in a user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSucess(false);
            String reason = "USER_NOT_VERIFIED";
            if(ex.isNewEmailSent()){
                reason += "_NEW_EMAIL_SENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt == null){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSucess(true);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Post mapping to verify the email of an account using the emailed token.
     * @param token The token emailed for verification. This is not the same as a
     *              authentication JWT.
     * @return 200 if successful. 409 if failure.
     */
    @Hidden
    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if(userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Gets the profile of the currently logged-in user and returns it.
     * @param user The authentication principal object.
     * @return The user profile.
     */
    @Operation(summary = "Get the logged in user profile", description = "Gets the profile of the currently logged in user.")
    @GetMapping("/me")
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

    /**
     * Sends an email to the user with a link to reset their password.
     * @param email The email to reset.
     * @return Ok if sent, bad request if email not found.
     */
    @Operation(summary = "Send password reset email", description = "Sends an email to the user with a link to reset their password.")
    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try{
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        }catch (EmailNotFoundException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (EmailFailureException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Resets the users password with the given token and password.
     * @param body The information for the password reset.
     * @return Okay if password was set.
     */
    @Hidden
    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }
}
