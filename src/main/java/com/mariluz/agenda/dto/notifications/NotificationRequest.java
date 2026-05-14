package com.mariluz.agenda.dto.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NotificationRequest {

    private String email;
    private String title;
    private String body;

    @JsonProperty("isCancellation")
    private boolean isCancellation; // atributo para cambiar la template del correo en caso de ser
}
