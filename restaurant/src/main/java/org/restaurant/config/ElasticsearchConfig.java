package org.restaurant.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class ElasticsearchConfig {

    private static String HTTP_CA_FINGERPRINT = "74c51955f4bc0ff3a63f26ec6a240486b16a0d49a44955bcb8a925803d8afb91";

    @Bean
    public RestClient restClient() {
        SSLContext sslContext = TransportUtils.sslContextFromCaFingerprint(HTTP_CA_FINGERPRINT);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "C2b*gL3HsxP=zzrbc0DM"));

        return RestClient.builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(
                        hc -> hc
                                .setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider)
                ).build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport() {
        return new RestClientTransport(restClient(), new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return new ElasticsearchClient(elasticsearchTransport());
    }
}

