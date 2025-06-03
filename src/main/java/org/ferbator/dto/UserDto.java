package org.ferbator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ferbator.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}

