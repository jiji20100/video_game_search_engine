package fr.lernejo.search.api;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GameSearchController {

    private final RestHighLevelClient client;

    public GameSearchController(RestHighLevelClient client) {
        this.client = client;
    }

    @GetMapping("/api/games")
    public List<Map<String, Object>> searchGames(@RequestParam String query, @RequestParam(defaultValue = "10") int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("games"); // Assuming the index is named "games"
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.queryStringQuery(query));
        searchSourceBuilder.size(size);

        searchRequest.source(searchSourceBuilder);

        var searchResponse = client.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
        return Arrays.stream(searchResponse)
            .map(SearchHit::getSourceAsMap)
            .collect(Collectors.toList());
    }
}
