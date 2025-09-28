package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.MathService;

import java.util.Map;

@RestController
@RequestMapping("/info")
@Tag(name = "Info Controller", description = "Контроллер для получения информации о приложении")

public class InfoController {
    private final MathService mathService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.port}")
    private int serverPort;

    public InfoController(MathService mathService) {
        this.mathService = mathService;
    }

    @Operation (summary = "Получить порт приложения")
    @GetMapping("/port")
    public ResponseEntity<Integer> getPort() {
        return ResponseEntity.ok(serverPort);
    }

    @Operation (summary = "Вычислить сумму исправленным оригинальным методом (Stream.iterate)")
    @GetMapping ("/sum/original")
    public ResponseEntity<Long> calculateModifiedOriginalSum() {
        logger.info("Was invoked endpoint for calculate sum using original method");

        long startTime = System.nanoTime();
        long result = mathService.calculateModifiedOriginalSum();
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        logger.info("Original sum calculated: {} (took {} ms)",result, elapsedTime);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Сравнить производительность всех методов вычисления суммы")
    @GetMapping("/sum/compare")
    public ResponseEntity<Map<String,Map<String,Object>>> compareSumMethods(){
        logger.info("Was invoked endpoint for compare sum calculation methods");

        Map<String,Map<String,Object>> results = mathService.compareAllMethods();

        logger.info("Comparison completed. Fastest method: {}",
                results.get("fastest_method").get("name"));
        return ResponseEntity.ok(results);
    }

    @Operation (summary = "Вычислить сумму самым быстрым методом (математическая формула)")
    @GetMapping ("/sum/fastest")
    public ResponseEntity<Long> calculateFastestSum(){
        logger.info("Was invoked endpoint for calculate sum using fastest method");

        long startTime = System.nanoTime();
        long result = mathService.calculateMathematicalSum();
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;

        logger.info("Fastest sum calculated: {} (took {} ms)",result, elapsedTime);
        return ResponseEntity.ok(result);
    }

}
