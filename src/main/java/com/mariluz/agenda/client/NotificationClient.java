/*
    Servicio dedicado a comunicarse con MS-Notifications -> usando  RestClient
    RestClient es mas nuevo y tiene mejor soporte para las nuevas versiones de Spring

    NOTA: Ya que el servicio de resend no tiene
    configurado un dominio (correo oficial de empresa/tienda)
    el correo unicamente puede ser enviado al duenio de la api(brunomcalderonv@gmail.com)
    -> por lo que cualquier intento de enviar correo a otra direccion va a  dar error

*/

package com.mariluz.agenda.client;

import com.mariluz.agenda.dto.notifications.NotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient(
        @Value("${notification.service.url}") String notificationServiceUrl
    ) {
        // Asignamos URL base para la conexion con el microservicio
        this.restClient = RestClient.builder()
            .baseUrl(notificationServiceUrl)
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
                .toBodilessEntity(); // Retorna la respuesta sin cuerpo (solo el status 200)
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
