package ru.alfabank.practice.chulyukovnv.bankonboarding.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.client.DadataAddressRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.client.DadataAddressResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DadataClientWireMockTest {

    private WireMockServer wireMockServer;
    private DadataClient dadataClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        wireMockServer.stubFor(post(urlEqualTo("/rs/suggest/address"))
                .withHeader("Authorization", equalTo("Token test-token"))
                .withRequestBody(equalToJson("{\"query\":\"Москва\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "suggestions": [
                                    {
                                      "value": "Москва, Россия",
                                      "data": {
                                        "fias_level": "9"
                                      }
                                    }
                                  ]
                                }
                                """)));

        dadataClient = Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(DadataClient.class, wireMockServer.baseUrl());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void suggestAddress_shouldSendAuthorizationHeaderAndParseResponse() {
        DadataAddressResponse response = dadataClient.suggestAddress("Token test-token", new DadataAddressRequest("Москва"));

        assertNotNull(response);
        assertNotNull(response.getSuggestions());
        assertEquals(1, response.getSuggestions().size());
        assertEquals("Москва, Россия", response.getSuggestions().getFirst().getValue());
        assertEquals("9", response.getSuggestions().getFirst().getData().getFiasLevel());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/rs/suggest/address"))
                .withHeader("Authorization", equalTo("Token test-token"))
                .withRequestBody(equalToJson("{\"query\":\"Москва\"}")));
    }
}
