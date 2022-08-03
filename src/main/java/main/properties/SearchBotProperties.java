package main.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "searchbot")
public class SearchBotProperties {

    private SiteParams[] links;

    private String userAgent;

    private String pathToWebInterface;

    private int threadNumber;

    public SiteParams[] getLinks() {
        return links;
    }

    public void setLinks(SiteParams[] links) {
        this.links = links;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getPathToWebInterface() {
        return pathToWebInterface;
    }

    public void setPathToWebInterface(String pathToWebInterface) {
        this.pathToWebInterface = pathToWebInterface;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }
}
