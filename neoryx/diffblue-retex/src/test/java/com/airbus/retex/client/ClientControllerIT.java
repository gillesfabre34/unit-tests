package com.airbus.retex.client;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.persistence.client.ClientRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ClientControllerIT extends BaseControllerTest {

    private static final String API_CLIENTS = "/api/clients";

    @Autowired
    private ClientRepository clientRepository;


    @Test
    void getAllClients_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_CLIENTS);
        expectedStatus = HttpStatus.OK;
        checkGetAllClient(hasSize((int) clientRepository.count()));
    }

    @Test
    void getAllClients_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_CLIENTS);
        expectedStatus = HttpStatus.FORBIDDEN;
        abstractCheck();
    }

    @Test
    public void getAllClients_NameFiltered_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("name", "Client");
        expectedStatus = HttpStatus.OK;

        checkGetAllClient(List.of(dataset.client_1, dataset.client_2));
    }

    @Test
    public void getAllClients_NameFiltered_NoResult() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("name", "aaa");
        expectedStatus = HttpStatus.OK;

        checkGetAllClient(List.of());
    }



    private void checkGetAllClient(List<Client> contains) throws Exception {
        withRequest = get(API_CLIENTS);

        Integer[] containsIds = contains.stream().map(client -> Integer.valueOf(client.getId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);


        abstractCheck()
                .andExpect(jsonPath("$[*].id", hasItems(containsIds)));
    }

    private void checkGetAllClient(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(API_CLIENTS);

        abstractCheck()
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", resultsMatcher));
    }
}
