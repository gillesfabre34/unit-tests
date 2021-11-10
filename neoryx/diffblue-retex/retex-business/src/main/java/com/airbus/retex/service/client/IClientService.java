package com.airbus.retex.service.client;

import com.airbus.retex.business.dto.client.ClientFilteringDto;
import com.airbus.retex.business.dto.client.ClientLightDto;

import java.util.List;

public interface IClientService {

    /**
     * Get all Client
     *
     * @return all Client Dto
     */
    List<ClientLightDto> getAllClientName(ClientFilteringDto filtering);
}
