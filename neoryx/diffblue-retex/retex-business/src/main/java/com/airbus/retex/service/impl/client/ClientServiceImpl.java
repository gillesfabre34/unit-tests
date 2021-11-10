package com.airbus.retex.service.impl.client;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.client.ClientFilteringDto;
import com.airbus.retex.business.dto.client.ClientLightDto;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.client.specification.ClientSpecification;
import com.airbus.retex.persistence.client.ClientRepository;
import com.airbus.retex.service.client.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClientServiceImpl implements IClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DtoConverter dtoConverter;


    @Override
    public List<ClientLightDto> getAllClientName(ClientFilteringDto filtering) {
        return dtoConverter.convert(clientRepository.findAll(buildSpecification(filtering)),ClientLightDto::new);
    }

    /**
     * @param filtering
     * @return
     */
    private Specification<Client> buildSpecification(ClientFilteringDto filtering) {
        Specification<Client> specification = Specification.where(null);

        if (null != filtering.getName()) {
            specification = specification.and(ClientSpecification.filterByName(filtering.getName()));
        }

        return specification;
    }
}