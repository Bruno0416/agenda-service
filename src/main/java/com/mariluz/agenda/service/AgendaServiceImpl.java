package com.mariluz.agenda.service;

import com.mariluz.agenda.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AgendaServiceImpl implements AgendaService {

    // @Autowired
    // private JwtUtil jwtUtil;

    @Override
    public User me() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        return currentUser;
    }
}
