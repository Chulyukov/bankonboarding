package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.alfabank.practice.chulyukovnv.bankonboarding.client.DadataClient;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.DadataAddressRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.DadataAddressResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.EmptyDeliveryAddressException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.IncorrectDeliveryAddress;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DadataServiceTest {

    @Mock
    private DadataClient dadataClient;

    private DadataService dadataService;

    @BeforeEach
    void setUp() {
        dadataService = new DadataService(dadataClient);
        ReflectionTestUtils.setField(dadataService, "dadataToken", "test-token");
    }

    @Test
    void validateDeliveryAddress_shouldThrowEmptyDeliveryAddressExceptionWhenAddressIsNull() {
        assertThrows(EmptyDeliveryAddressException.class, () -> dadataService.validateDeliveryAddress(null));
    }

    @Test
    void validateDeliveryAddress_shouldThrowEmptyDeliveryAddressExceptionWhenAddressIsBlank() {
        assertThrows(EmptyDeliveryAddressException.class, () -> dadataService.validateDeliveryAddress("   "));
    }

    @Test
    void validateDeliveryAddress_shouldThrowIncorrectDeliveryAddressWhenNoSuggestionHasLevel9() {
        DadataAddressResponse response = new DadataAddressResponse();
        response.setSuggestions(List.of(buildSuggestion("8"), buildSuggestion("7")));
        when(dadataClient.suggestAddress(eq("Token test-token"), any(DadataAddressRequest.class))).thenReturn(response);

        assertThrows(IncorrectDeliveryAddress.class, () -> dadataService.validateDeliveryAddress("г Москва, ул Тверская"));
    }

    @Test
    void validateDeliveryAddress_shouldAcceptAddressWhenAnySuggestionHasLevel9() {
        DadataAddressResponse response = new DadataAddressResponse();
        response.setSuggestions(List.of(buildSuggestion("8"), buildSuggestion("9")));
        when(dadataClient.suggestAddress(eq("Token test-token"), any(DadataAddressRequest.class))).thenReturn(response);

        assertDoesNotThrow(() -> dadataService.validateDeliveryAddress("г Москва, ул Тверская, д 1"));
        verify(dadataClient).suggestAddress(eq("Token test-token"), any(DadataAddressRequest.class));
    }

    private DadataAddressResponse.Suggestion buildSuggestion(String fiasLevel) {
        DadataAddressResponse.Suggestion suggestion = new DadataAddressResponse.Suggestion();
        DadataAddressResponse.AddressData data = new DadataAddressResponse.AddressData();
        data.setFiasLevel(fiasLevel);
        suggestion.setData(data);
        return suggestion;
    }
}
