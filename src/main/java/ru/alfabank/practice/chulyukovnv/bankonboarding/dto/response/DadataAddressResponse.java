package ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class DadataAddressResponse {
    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion {
        private String value;
        private AddressData data;
    }

    @Data
    public static class AddressData {
        @JsonProperty("fias_level")
        private String fiasLevel;
    }
}
