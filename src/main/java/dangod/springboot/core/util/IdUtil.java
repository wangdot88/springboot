package dangod.springboot.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class IdUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final Random RANDOM = new Random();

    public static String generateId(String prefix) {
        return prefix + FORMATTER.format(LocalDateTime.now()) + String.format("%04d", RANDOM.nextInt(10000));
    }

    public static String generateMemberNo() {
        return generateId("M");
    }

    public static String generateCardNo() {
        return generateId("C");
    }

    public static String generateReservationNo() {
        return generateId("R");
    }

    public static String generatePlanNo() {
        return generateId("P");
    }

    public static String generateNotificationNo() {
        return generateId("N");
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
