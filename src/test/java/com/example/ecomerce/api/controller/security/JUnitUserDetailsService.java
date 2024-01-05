package com.example.ecomerce.api.controller.security;

import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to provide spring with Authentication Principals for unit testing.
 */
@Service
@Primary
public class JUnitUserDetailsService implements UserDetailsService {

    /** The Local User DAO. */
    @Autowired
    private LocalUserDao localUserDao;

    /**
     * {@inheritDoc}
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(username);
        if(opUser.isPresent()) {
            return opUser.get();
        }
        return null;
    }


}
