package be.vanin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by ben on 11/2/16.
 */
@org.springframework.context.annotation.Configuration
public class Configuration extends WebMvcConfigurerAdapter {

    @Value("${boardbooks.location}")
    private String boardBooksLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!boardBooksLocation.endsWith("/")) {
            boardBooksLocation = boardBooksLocation + "/";
        }
        registry.addResourceHandler("/assets/**").addResourceLocations("file://" + boardBooksLocation);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/assets/**").allowedOrigins("http://localhost:8080");
        registry.addMapping("/boardbook/**").allowedOrigins("http://localhost:8080");
    }

}
