package be.vanin;

import org.apache.log4j.spi.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.logging.Logger;

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
        FileSystemResource fileSystemResource = new FileSystemResource(boardBooksLocation);
        try {
            registry.addResourceHandler("/assets/**").addResourceLocations(fileSystemResource.getURI().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/assets/**").allowedOrigins("http://localhost:8080");
        registry.addMapping("/boardbook/**").allowedOrigins("http://localhost:8080");
    }

}
