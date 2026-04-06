package com.Sridevi.Campushelpdesk.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class UserRequest {
    private String email;
    private String password;
}
