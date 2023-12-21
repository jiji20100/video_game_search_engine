package fr.lernejo.search.api;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {

    @Value("${elasticsearch.host:localhost}")
    private final String host;
    @Value("${elasticsearch.port:9200}")
    private final int port;
    @Value("${elasticsearch.username:elastic}")
    private final String username;
    @Value("${elasticsearch.password:password}")
    private final String password;

    public ElasticSearchConfiguration(
        @Value("${elasticsearch.host:localhost}") String host,
        @Value("${elasticsearch.port:9200}") int port,
        @Value("${elasticsearch.username:elastic}") String username,
        @Value("${elasticsearch.password:password}") String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }




    @Bean
    public RestHighLevelClient restHighLevelClient () {
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(host,port,"http")
            ).setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(
                    new BasicCredentialsProvider() {{
                        setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username,password));
                    }}
                )
            )
        );
    }
}
