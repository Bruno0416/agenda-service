/*
Servicio dedicado a comunicarse con MS-Notifications -> usando  RestClient
RestClient es mas nuevo y tiene mejor soporte para las nuevas versiones de Spring
*/

package com.mariluz.agenda.client;

import com.mariluz.agenda.dto.notifications.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient() {
        // Asignamos URL base para la conexion con el microservicio
        this.restClient = RestClient.builder()
            .baseUrl("http://localhost:8082/notifications")
            .build();
    }

    public void sendReservationEmail(
        String email,
        String title,
        String body,
        boolean isCancellation
    ) {
        // Crear contenido para la solicitud
        NotificationRequest content = NotificationRequest.builder()
            .email(email)
            .title(title)
            .body(body)
            .isCancellation(isCancellation)
            .build();

        try {
            restClient
                .post()
                .uri("/reservation")
                .body(content)
                .retrieve()
                .toBodilessEntity(); // Restorna la respuesta sin cuerpo (solo el status 200)
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
