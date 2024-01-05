package com.example.ecomerce.api.controller.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import com.example.ecomerce.service.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing the JWTRequestFilter.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {

    /** Mocked MVC. */
    @Autowired
    private MockMvc mvc;
    /** The JWT Service. */
    @Autowired
    private JWTService jwtService;
    /** The Local User DAO. */
    @Autowired
    private LocalUserDao localUserDao;
    /** The path that should only allow authenticated users. */
    private static final String AUTHENTICATED_PATH = "/api/auth/me";

    /**
     * Tests that unauthenticated requests are rejected.
     * @throws Exception
     */
    @Test
    public void testUnauthenticationRequest() throws Exception{
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests that bad tokens are rejected.
     * @throws Exception
     */
    @Test
    public void testBadToken() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenThatIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests unverified users who somehow get a jwt are rejected.
     * @throws Exception
     */
    @Test
    public void testUnverifiedUser() throws Exception {
        LocalUser user = localUserDao.findByUsernameIgnoreCase("UserB").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer "+token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests the successful authentication.
     * @throws Exception
     */
    @Test
    @WithUserDetails("UserA")
    public void testSucessful() throws Exception {
        LocalUser user = localUserDao.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer "+token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
