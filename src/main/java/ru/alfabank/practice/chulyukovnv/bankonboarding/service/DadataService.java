package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.client.DadataClient;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.DadataAddressRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.DadataAddressResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.EmptyDeliveryAddressException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.IncorrectDeliveryAddress;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DadataService {

    private final DadataClient dadataClient;
    @Value("${dadata.api.token}")
    private String dadataToken;

    public void validateDeliveryAddress(String deliveryAddress) {
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new EmptyDeliveryAddressException();
        }
        DadataAddressRequest dadataAddressRequest = new DadataAddressRequest(deliveryAddress);
        String authHeader = "Token " + dadataToken;

        DadataAddressResponse dadataAddressResponse = dadataClient.suggestAddress(authHeader, dadataAddressRequest);

        boolean isValidLevel9Found = dadataAddressResponse.getSuggestions() != null && dadataAddressResponse.getSuggestions().stream()
                .map(DadataAddressResponse.Suggestion::getData)
                .filter(Objects::nonNull)
                .anyMatch(data -> "9".equals(data.getFiasLevel()));

        if (!isValidLevel9Found) {
            throw new IncorrectDeliveryAddress();
        }
    }


}
