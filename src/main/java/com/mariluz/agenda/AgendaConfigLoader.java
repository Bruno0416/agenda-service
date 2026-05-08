package com.mariluz.agenda;

import com.mariluz.agenda.model.AgendaConfig;
import com.mariluz.agenda.repository.AgendaConfigRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgendaConfigLoader {

    @Bean
    CommandLineRunner init(AgendaConfigRepository repo) {
        return args -> {
            // Dias de trabajo
            List<Integer> workDays = List.of(1, 2, 4, 5);
            // Crear configuracion DEFAULT agenda
            if (repo.count() == 0) {
                repo.save(
                    AgendaConfig.builder()
                        //  hr / min / seg
                        .startWorkTime(LocalTime.of(9, 00, 0))
                        .endWorkTime(LocalTime.of(14, 30, 0))
                        .slotDuration(20)
                        .workDays(workDays)
                        .updatedAt(LocalDateTime.now())
                        .build()
                );
            }
        };
    }
}
