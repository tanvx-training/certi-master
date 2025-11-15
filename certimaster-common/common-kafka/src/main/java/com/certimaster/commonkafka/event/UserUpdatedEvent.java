package com.certimaster.commonkafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Event published when user information is updated
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserUpdatedEvent extends BaseEvent {

    private Long userId;
    private Map<String, Object> updatedFields;
    private String updatedBy;

    public UserUpdatedEvent(Long userId, Map<String, Object> updatedFields) {
        super();
        this.userId = userId;
        this.updatedFields = updatedFields;
        this.setEventType("USER_UPDATED");
        init();
    }
}
