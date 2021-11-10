package com.airbus.retex.business.converter.dtoconverter;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.user.UserDto;
import com.airbus.retex.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DtoConverterMapAddTU {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DtoConverter dtoConverter;

    private long nextIt = 1;
    private Set<User> referenceUserList;
    private Set<User> userList;

    @BeforeEach
    public void before() {
        userList = Stream.of(nextIt++, nextIt++, nextIt++, nextIt++)
                .map(id -> {
                    User user = new User();
                    user.setId(id);
                    user.setEmail(id.toString());
                    return user;
                })
                .collect(Collectors.toSet());
        referenceUserList = new HashSet<>(userList);

        when(entityManager.getReference(Mockito.eq(User.class), Mockito.any(Object.class))).thenAnswer(invocation -> {
            Optional<User> userOpt = referenceUserList.stream().filter(user -> user.getId().equals(invocation.getArgument(1))).findFirst();
            if(userOpt.isPresent()) {
                return userOpt.get();
            } else {
                User user = new User();
                user.setId(invocation.getArgument(1));
                return user;
            }
        });
    }

    /*
     * Tests using ids as source
     */

    @Test
    public void mapIdsAdd_existing() {
        checkMapIdsAdd(List.of(1L, 3L), List.of(1L, 2L, 3L, 4L));
    }

    @Test
    public void mapIdsAddRemove_existing() {
        checkMapIdsAddRemove(List.of(1L, 3L), List.of(1L, 3L));
    }

    @Test
    public void mapIdsAdd_existingAndNew() {
        checkMapIdsAdd(List.of(1L, 3L, 5L, 6L), List.of(1L, 2L, 3L, 4L, 5L, 6L));
    }

    @Test
    public void mapIdsAddRemove_existingAndNew() {
        checkMapIdsAddRemove(List.of(1L, 3L, 5L, 6L), List.of(1L, 3L, 5L, 6L));
    }

    @Test
    public void mapIdsAdd_new() {
        checkMapIdsAdd(List.of(5L, 6L), List.of(1L, 2L, 3L, 4L, 5L, 6L));
    }

    @Test
    public void mapIdsAddRemove_new() {
        checkMapIdsAddRemove(List.of(5L, 6L), List.of(5L, 6L));
    }

    @Test
    public void mapIdsAdd_nothing() {
        checkMapIdsAdd(List.of(), List.of(1L, 2L, 3L, 4L));
    }

    @Test
    public void mapIdsAddRemove_nothing() {
        checkMapIdsAddRemove(List.of(), List.of());
    }

    /*
     * Tests using collection as source
     */

    @Test
    public void mapEntitiesAddUpdate_updateTwo() {
        checkMapEntitiesAddUpdate(List.of(1L, 3L), List.of(1L, 2L, 3L, 4L));
    }

    @Test
    public void mapEntitiesAddUpdate_addOne() {
        checkMapEntitiesAddUpdate(List.of(5L), List.of(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    public void mapEntitiesAddUpdate_addAndUpdate() {
        checkMapEntitiesAddUpdate(List.of(1L, 5L), List.of(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    public void mapEntitiesAddUpdateRemove_removeOne() {
        checkMapEntitiesAddUpdateRemove(List.of(1L, 2L, 3L), List.of(1L, 2L, 3L));
    }

    @Test
    public void mapEntitiesAddUpdateRemove_removeAll() {
        checkMapEntitiesAddUpdateRemove(List.of(), List.of());
    }

    @Test
    public void mapEntitiesAddUpdateRemove_removeAndAddAndUpdate() {
        checkMapEntitiesAddUpdateRemove(List.of(1L, 2L, 3L, 5L), List.of(1L, 2L, 3L, 5L));
    }

    /*
     * Test utilities methods
     */

    private void checkMapIdsAdd(List<Long> idsToAdd, List<Long> expectedIds) {
        dtoConverter.mapIdsAdd(idsToAdd, userList, User.class);

        checkMapIdsAssert(expectedIds);
    }

    private void checkMapIdsAddRemove(List<Long> idsToAdd, List<Long> expectedIds) {
        dtoConverter.mapIdsAddRemove(idsToAdd, userList, User.class);

        checkMapIdsAssert(expectedIds);
    }

    private void checkMapIdsAssert(List<Long> expectedIds) {
        assertThat(userList, hasSize(expectedIds.size()));

        for(var id : expectedIds) {
            assertThat("No item with id "+id,
                    userList,
                    hasItem(hasProperty("id", equalTo(id)))
            );
            if(referenceUserList.contains(id)) {
                Optional<User> userOptional = userList.stream().filter(user -> id.equals(user.getId())).findFirst();
                assertThat(userOptional.isPresent(), is(true));
                assertThat("No equal item found for "+userOptional.get(),
                        referenceUserList, hasItem(equalTo(userOptional.get())));
            }
        }
    }

    private void checkMapEntitiesAddUpdate(List<Long> idsToAddUpdate, List<Long> expectedIds) {
        List<UserDto> dtos = idsToAddUpdate.stream()
                .map(id -> {
                    UserDto dto = new UserDto();
                    dto.setId(id);
                    dto.setEmail("updated");
                    return dto;
                })
                .collect(Collectors.toList());

        dtoConverter.mapEntitiesAddUpdate(dtos, userList, User.class);

        checkMapEntitiesAssert(idsToAddUpdate, expectedIds);
    }

    private void checkMapEntitiesAddUpdateRemove(List<Long> idsToAddUpdate, List<Long> expectedIds) {
        List<UserDto> dtos = idsToAddUpdate.stream()
                .map(id -> {
                    UserDto dto = new UserDto();
                    dto.setId(id);
                    dto.setEmail("updated");
                    return dto;
                })
                .collect(Collectors.toList());

        dtoConverter.mapEntitiesAddUpdateRemove(dtos, userList, User.class);

        checkMapEntitiesAssert(idsToAddUpdate, expectedIds);
    }

    private void checkMapEntitiesAssert(List<Long> idsToAddUpdate, List<Long> expectedIds) {
        checkMapIdsAssert(expectedIds);

        for(var user : referenceUserList) {
            if(idsToAddUpdate.contains(user.getId())) {
                assertThat("Email not updated for id "+user.getId(),
                        "updated", equalTo(user.getEmail()));
            } else {
                assertThat("Email should not have been change",
                        user.getId().toString(), equalTo(user.getEmail()));
            }
        }
    }
}
