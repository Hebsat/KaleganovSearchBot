package main.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "searchbot")
@Getter
@Setter
public class SearchBotProperties {

    private SiteParams[] links;
    private String userAgent;
    private int threadNumber;
}
