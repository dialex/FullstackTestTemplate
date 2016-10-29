package helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerUtils {
    public static String extractIP() {
        try {
            String DOCKER_IP = System.getenv("DOCKER_HOST");

            String IP_ADDRESS_PATTERN =
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

            Pattern pattern = Pattern.compile(IP_ADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(DOCKER_IP);
            if (matcher.find()) {
                return matcher.group();
            } else {
                return "localhost";
            }
        } catch (NullPointerException e) {
            return "localhost";
        }
    }
}
