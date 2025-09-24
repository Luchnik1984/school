package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Service
public class MathService {
    private final Logger logger = LoggerFactory.getLogger(MathService.class);
    private static final long LIMIT = 1_000_000;

    /**
     * Оригинальный метод Stream.iterate
     */
   public int calculateOriginalSum(){
       logger.debug("Calculating sum using original Stream.iterate method");
       return Stream.iterate(1, a->a+1)
               .limit(LIMIT)
               .reduce(0,(a,b)->a+b);
   }

    /**
     * Оригинальный метод из задания (исправленный)
     */
    public long calculateModifiedOriginalSum() {
        logger.debug("Calculating sum using modified original Stream.iterate method");
        return Stream.iterate(1L, a -> a + 1L) // Используем Long вместо Integer
                .limit(LIMIT)
                .reduce(0L, Long::sum); // Используем Long::sum вместо (a, b) -> a + b
    }


    /**
     * Математическая формула Гаусса S = n * (n + 1) / 2
     */

    public long calculateMathematicalSum(){
        logger.debug("Calculating sum using mathematical formula");
        return LIMIT*(LIMIT+1)/2;
    }

    /**
     * Параллельный стрим
     */

    public long calculateParallelStreamSum(){
        logger.debug("Calculating sum using parallel LongStream");
        return LongStream.rangeClosed(1,LIMIT)
                .parallel()
                .sum();
    }

    /**
     * Обычный цикл
     */
    public long calculateLoopSum(){
        logger.debug("Calculating sum using simple for-loop");
        long sum = 0;
        for(long i = 1; i <= LIMIT; i++){
            sum += i;
        }
        return sum;
    }

    /**
     * Измерение времени выполнения метода
     */

    private Map<String, Object> measurePerformance(SumCalculator calculator, String methodName) {

        calculator.calculate();
        calculator.calculate();

        long startTime = System.nanoTime();
        long result = calculator.calculate();
        long endTime = System.nanoTime();

        long durationNanos = endTime - startTime;
        long durationMs = TimeUnit.NANOSECONDS.toMillis(durationNanos);

        Map<String, Object> measurement = new HashMap<>();
        measurement.put("result", result);
        measurement.put("time_ns", durationNanos);
        measurement.put("time_ms", durationMs);
        measurement.put("method_name", methodName);

        logger.debug("{} - Result: {}, Time: {} ns ({} ms)",
                methodName, result, durationNanos, durationMs);

        return measurement;
    }

    /**
     * Сравнение производительности всех методов
     */
    public Map<String, Map<String, Object>> compareAllMethods() {
        logger.info("Starting performance comparison of all sum calculation methods");

        Map<String, Map<String, Object>> results = new LinkedHashMap<>();

        results.put("original_stream", measurePerformance(this::calculateOriginalSum, "Original Stream"));
        results.put("modified_original_stream", measurePerformance(this::calculateModifiedOriginalSum, "Modified_original Stream"));
        results.put("mathematical_formula", measurePerformance(this::calculateMathematicalSum, "Mathematical Formula"));
        results.put("parallel_stream", measurePerformance(this::calculateParallelStreamSum, "Parallel Stream"));
        results.put("for_loop", measurePerformance(this::calculateLoopSum, "For Loop"));

        // Определяем самый быстрый метод
        String fastestMethod = findFastestMethod(results);

        results.put("fastest_method", Map.of("name", fastestMethod,
                "time_ns", results.get(fastestMethod).get("time_ns")));

        logger.info("Performance comparison completed. Fastest method: {}", fastestMethod);
        return results;
    }

    /**
     * Поиск самого быстрого метода
     */
    private String findFastestMethod(Map<String, Map<String, Object>> results) {
        return results.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("fastest_method"))
                .min((e1, e2) -> Long.compare(
                        (Long) e1.getValue().get("time_ns"),
                        (Long) e2.getValue().get("time_ns")
                ))
                .map(Map.Entry::getKey)
                .orElse("mathematical_formula");
    }

    @FunctionalInterface
    private interface SumCalculator {
        long calculate();
    }
}
