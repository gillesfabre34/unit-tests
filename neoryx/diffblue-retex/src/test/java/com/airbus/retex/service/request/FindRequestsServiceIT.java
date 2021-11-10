package com.airbus.retex.service.request;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.request.RequestDto;
import com.airbus.retex.business.dto.request.RequestFilteringDto;
import com.airbus.retex.business.dto.request.RequestSortingValues;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.translate.ITranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FindRequestsServiceIT extends AbstractServiceIT {

    @Autowired
    private IRequestService requestService;

    @Autowired
    private ITranslateService translateService;

    private RequestFilteringDto withDto = new RequestFilteringDto();

    private User requester;
    private static final String BASE_NAME_REQUEST = "requestname";
    private static final String REQ_NAME_1 = BASE_NAME_REQUEST + 1;
    private static final String REQ_NAME_2 = BASE_NAME_REQUEST + 2;
    private Request request1;
    private Request request2;
    private List<Request> expectedRequest = new ArrayList<>();
    private List<Request> notExpectedRequest = new ArrayList<>();
    private User sortRequester1;
    private User sortRequester2;

    @BeforeEach
    public void before() {

        requester = dataSetInitializer.createUser();

        Origin origin1 = dataset.ORIGIN_CIVP;
        Origin origin2 = dataset.ORIGIN_RETEX;
        request1 = dataSetInitializer.createRequest(REQ_NAME_1, request -> {
            request.setRequester(requester);
            request.setAirbusEntity(dataset.airbusEntity_canada);
            request.setStatus(EnumStatus.DONE);
            request.setOrigin(origin1);
        });

        request2 = dataSetInitializer.createRequest(REQ_NAME_2, request -> {
            request.setRequester(requester);
            request.setAirbusEntity(dataset.airbusEntity_canada);
            request.setStatus(EnumStatus.IN_PROGRESS);
            request.setOrigin(origin2);
        });

    }

    @Test
    public void find_withRequesterId_ok () throws Exception {
        withDto.setRequesterIds(List.of(dataset.user_superAdmin.getId()));

        expectedRequest = List.of(dataset.request_1, dataset.request_2,dataset.request_update,dataset.request_save);

        check();
    }

    @Test
    public void find_withOtherRequesterId_ok () throws Exception {
        withDto.setRequesterIds(List.of(requester.getId()));

        expectedRequest.add(dataSetInitializer.createRequest("request", request -> {
            request.setRequester(requester);
        }));
        expectedRequest.add(request1);
        expectedRequest.add(request2);
        notExpectedRequest = List.of(dataset.request_1, dataset.request_2);

        check();
    }

    @Test
    public void find_withAllFiltersSet_ok () throws Exception {

        withDto.setRequesterIds(List.of((requester.getId())));
        withDto.setName(REQ_NAME_1);
        withDto.setAirbusEntityIds(List.of(dataset.airbusEntity_canada.getId(), dataset.airbusEntity_france.getId()));
        withDto.setOriginIds(List.of(dataset.ORIGIN_CIVP.getId(), dataset.ORIGIN_ISIR.getId()));
        withDto.setStatuses(List.of(EnumStatus.DONE, EnumStatus.TO_DO));
        withDto.setSortBy(RequestSortingValues.name);
        withDto.setSortDirection(Sort.Direction.ASC);

        expectedRequest = List.of(request1);

        notExpectedRequest = List.of(dataset.request_1, dataset.request_2, request2);

        check();
    }

    @Test
    public void find_withNameStatusesAndAirbusEntityFiltersSortByAirbusEntity_ok () throws Exception {

        setWithDtoSample();

        expectedRequest = List.of(request1, request2);

        notExpectedRequest = List.of(dataset.request_1, dataset.request_2);

        PageDto<RequestDto> page = check();

        // Verify sort on name ASC
        List<RequestDto> result = page.getResults();
        {
            RequestDto first = result.get(0);
            RequestDto last = result.get(1);
            String name1 = translateService.getFieldValue(first.getName().getClassName(),
                    first.getName().getEntityId(), first.getName().getField(), Language.languageFor(Locale.FRANCE));
            assertThat(name1, startsWith(REQ_NAME_1));
            String name2 = translateService.getFieldValue(last.getName().getClassName(),
                    last.getName().getEntityId(), last.getName().getField(), Language.languageFor(Locale.FRANCE));
            assertThat(name2, startsWith(REQ_NAME_2));
        }

        // sort DESC
        withDto.setSortDirection(Sort.Direction.DESC);
        page = check();
        result = page.getResults();
        {
            RequestDto first = result.get(0);
            RequestDto last = result.get(1);
            String name1 = translateService.getFieldValue(first.getName().getClassName(),
                    first.getName().getEntityId(), first.getName().getField(), Language.languageFor(Locale.FRANCE));
            assertThat(name1, startsWith(REQ_NAME_2));
            String name2 = translateService.getFieldValue(last.getName().getClassName(),
                    last.getName().getEntityId(), last.getName().getField(), Language.languageFor(Locale.FRANCE));
            assertThat(name2, startsWith(REQ_NAME_1));
        }

    }

    @Test
    public void find_withSort_ok () throws Exception {
        String name = "Common";
        generateRequestsForSort(name);
        withDto.setName(name);

        // Verify sort on
        // reference
        Function<RequestDto, String> getReference = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {
                return requestDto.getReference();
            }
        };
        verifySort(RequestSortingValues.reference, "reference1", "reference2", getReference);

        // origin
        Function<RequestDto, String> getOrigin = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {
                return requestDto.getOrigin().getName();
            }
        };
        verifySort(RequestSortingValues.origin, dataset.ORIGIN_CIVP.getName(), dataset.ORIGIN_RETEX.getName(), getOrigin);

        // airbusEntity
        Function<RequestDto, String> getAirbusEntity = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {

                return requestDto.getAirbusEntity().getCountryName();
            }
        };
        verifySort(RequestSortingValues.airbus_entity, dataset.airbusEntity_canada.getCountryName(), dataset.airbusEntity_france.getCountryName(), getAirbusEntity);

        // status
        Function<RequestDto, String> getStatus = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {
                return requestDto.getStatus().toString();
            }
        };
        verifySort(RequestSortingValues.status, EnumStatus.IN_PROGRESS.toString(), EnumStatus.TO_DO.toString(), getStatus);
        // requester
        Function<RequestDto, String> getRequester = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {
                return requestDto.getRequester().getFirstName();
            }
        };
        verifySort(RequestSortingValues.user, sortRequester1.getFirstName(), sortRequester2.getFirstName(), getRequester);

        // creationDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Function<RequestDto, String> getCreationDate = new Function<RequestDto, String>() {
            @Override
            public String apply(RequestDto requestDto) {
                return requestDto.getCreationDate().format(formatter);
            }
        };
        verifySort(RequestSortingValues.creation_date, expectedRequest.get(0).getCreationDate().format(formatter), expectedRequest.get(1).getCreationDate().format(formatter), getCreationDate);

    }

    /**
     * Verify that the result is sorted (ASC and DESC)
     * ASC -> first item has 'valMin' value, last item has 'valMax' value
     * DESC -> first item has 'valMax' value, last item has 'valMin' value
     * @param sortBy
     * @param valMin
     * @param valMax
     * @param fn
     * @throws Exception
     */
    private void verifySort(RequestSortingValues sortBy, String valMin, String valMax, Function<RequestDto, String> fn) throws Exception {
        withDto.setSortBy(sortBy);

        verifySortDirection(Sort.Direction.ASC, valMin, valMax, fn);
        verifySortDirection(Sort.Direction.DESC, valMax, valMin, fn);
    }

    /**
     * According to a sort direction, verify that the first item returned has the 'first' value and the last item has the 'last' value
     * @param direction
     * @param first expected value of the fist returned item
     * @param last expected value of the last returned item
     * @param fn
     * @throws Exception
     */
    private void verifySortDirection(Sort.Direction direction, String first, String last, Function<RequestDto, String> fn) throws Exception {
        withDto.setSortDirection(direction);
        PageDto<RequestDto> page = check();
        List<RequestDto> result = page.getResults();
        int count = result.size();
        RequestDto firstItem = result.get(0);
        RequestDto lastItem = result.get(count - 1);
        assertThat(fn.apply(firstItem), equalTo(first));
        assertThat(fn.apply(lastItem), equalTo(last));
    }

    /**
     * Generate some request to verify search sort
     */
    private void generateRequestsForSort(String commonName) {

        sortRequester1 = dataSetInitializer.createUser(dataset.airbusEntity_canada, user -> {
            user.setFirstName("sortRequester1");
        });

        sortRequester2 = dataSetInitializer.createUser(dataset.airbusEntity_canada, user -> {
            user.setFirstName("sortRequester2");
        });

        Origin origin1 = dataset.ORIGIN_CIVP;
        Origin origin2 = dataset.ORIGIN_RETEX;
        String reference1 = "reference1";
        String reference2 = "reference2";
        EnumStatus status1 = EnumStatus.TO_DO;
        EnumStatus status2 = EnumStatus.IN_PROGRESS;


        expectedRequest.add(
                dataSetInitializer.createRequest(commonName, request -> {
                    request.setRequester(sortRequester1);
                    request.setReference(reference1);
                    request.setAirbusEntity(dataset.airbusEntity_france);
                    request.setStatus(status1);
                    request.setOrigin(origin1);
                    request.setCreationDate(LocalDateTime.now().minusDays(1));
                })
        );
        expectedRequest.add(
                dataSetInitializer.createRequest(commonName, request -> {
                    request.setRequester(sortRequester2);
                    request.setReference(reference2);
                    request.setAirbusEntity(dataset.airbusEntity_canada);
                    request.setStatus(status2);
                    request.setOrigin(origin2);
                })
        );
    }

    private void setWithDtoSample(){
        withDto.setRequesterIds(List.of((requester.getId())));
        withDto.setName(BASE_NAME_REQUEST);
        withDto.setAirbusEntityIds(List.of(dataset.airbusEntity_canada.getId(), dataset.airbusEntity_france.getId()));
        withDto.setStatuses(List.of(EnumStatus.DONE, EnumStatus.IN_PROGRESS));
        withDto.setSortBy(RequestSortingValues.name);
        withDto.setSortDirection(Sort.Direction.ASC);
    }

    private PageDto<RequestDto>  check() throws Exception {
        PageDto<RequestDto> pageDto = requestService.findRequestsWithFilters(withDto, Language.FR);

        assertThat(pageDto.getTotalResults(), equalTo(Long.valueOf(expectedRequest.size())));

        expectedRequest.forEach(request -> {
            assertThat("No request with id "+request.getId()+" in results",
                    pageDto.getResults(), hasItems(hasProperty("id", equalTo(request.getId()))));
        });

        notExpectedRequest.forEach(request -> {
            assertThat("Request with id "+request.getId()+" must not be in results",
                    pageDto.getResults(), not(hasItems(hasProperty("id", equalTo(request.getId())))));
        });
        return pageDto;
    }
}
