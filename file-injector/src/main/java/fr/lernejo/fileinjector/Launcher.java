package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            RabbitTemplate rabbitTemplate = springContext.getBean(RabbitTemplate.class);
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GameInfo.class);
            List<GameInfo> messages = objectMapper.readValue(new File(args[0]), collectionType);
            for (GameInfo message : messages) {
                rabbitTemplate.convertAndSend("game_info", message, m -> {
                    m.getMessageProperties().setHeader("game_id", message.id());
                    m.getMessageProperties().setContentType("application/json");
                    return m;
                });
            }
        } catch (Exception e) { System.exit(2); }
    }
}
