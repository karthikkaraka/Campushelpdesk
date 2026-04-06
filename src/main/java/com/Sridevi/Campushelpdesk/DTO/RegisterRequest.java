package com.Sridevi.Campushelpdesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest{

        private String email;
        private String password;
        private String username;
        private String regno;
    }

