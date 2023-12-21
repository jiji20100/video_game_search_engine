package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class GameInfoListener {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public GameInfoListener(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(String message, @Header("game_id") String gameId) {
        try {
            IndexRequest indexRequest = new IndexRequest("games")
                .id(gameId)
                .source(message);
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            System.err.println("Error while indexing game " + gameId + ": " + e.getMessage());
        }
    }
}
