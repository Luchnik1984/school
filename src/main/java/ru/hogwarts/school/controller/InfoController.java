package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
@Tag(name = "Info Controller", description = "Контроллер для получения информации о приложении")

public class InfoController {

    @Value("${server.port}")
    private int serverPort;

    @Operation (summary = "Получить порт приложения")
    @GetMapping("/port")
    public ResponseEntity<Integer> getPort() {
        return ResponseEntity.ok(serverPort);
    }
}
