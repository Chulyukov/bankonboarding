package ru.alfabank.practice.chulyukovnv.bankonboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.DadataAddressRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.DadataAddressResponse;

@FeignClient(name = "dadataClient", url = "${dadata.api.url}")
public interface DadataClient {
    @PostMapping(value = "/rs/suggest/address", consumes = "application/json")
    DadataAddressResponse suggestAddress(
            @RequestHeader("Authorization") String authorization,
            @RequestBody DadataAddressRequest request
    );
}
