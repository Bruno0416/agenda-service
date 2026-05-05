package com.mariluz.agenda.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String email;
    private String role;
}
