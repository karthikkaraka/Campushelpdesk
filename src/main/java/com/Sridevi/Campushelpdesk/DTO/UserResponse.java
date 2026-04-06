package com.Sridevi.Campushelpdesk.DTO;

import com.Sridevi.Campushelpdesk.Enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private long userId;
    private String userName;
    private String email;
    private String regno;
    private AccountStatus accountStatus;
}
