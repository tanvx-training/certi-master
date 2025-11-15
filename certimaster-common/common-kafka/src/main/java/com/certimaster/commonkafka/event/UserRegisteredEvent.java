package com.certimaster.commonkafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserRegisteredEvent extends BaseEvent {

    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public UserRegisteredEvent(Long userId, String username, String email) {
        super();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.setEventType("USER_REGISTERED");
        init();
    }
}
