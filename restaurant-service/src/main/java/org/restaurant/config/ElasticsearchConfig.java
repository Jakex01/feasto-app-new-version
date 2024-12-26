//package org.restaurant.config;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.conn.ssl.NoopHostnameVerifier;
//import org.apache.http.conn.ssl.TrustAllStrategy;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.elasticsearch.client.RestClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.SSLContext;
//@Configuration
//public class ElasticsearchConfig {
//    @Bean
//    public RestClient restClient() {
//        try {
//            // Tworzenie SSLContext, który akceptuje wszystkie certyfikaty
//            SSLContext sslContext = SSLContextBuilder.create()
//                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
//                    .build();
//
//            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));
//
//            return RestClient.builder(new HttpHost("localhost", 9200, "https"))
//                    .setHttpClientConfigCallback(
//                            httpClientBuilder -> httpClientBuilder
//                                    .setSSLContext(sslContext)
//                                    .setDefaultCredentialsProvider(credentialsProvider)
//                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // Pomija weryfikację hosta
//                    ).build();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create RestClient with SSL configuration", e);
//        }
//    }
//
//    @Bean
//    public ElasticsearchTransport elasticsearchTransport() {
//        return new RestClientTransport(restClient(), new JacksonJsonpMapper());
//    }
//
//    @Bean
//    public ElasticsearchClient elasticsearchClient() {
//        return new ElasticsearchClient(elasticsearchTransport());
//    }
//}
