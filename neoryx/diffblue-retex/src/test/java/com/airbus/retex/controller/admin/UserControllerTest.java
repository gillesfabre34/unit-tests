package com.airbus.retex.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.airbus.retex.business.dto.user.UserDto;
import com.airbus.retex.configuration.filter.ApiKeyAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserControllerTest {
    @Test
    public void testRevokeUser() {
        UserController userController = new UserController();
        ResponseEntity<UserDto> actualRevokeUserResult = userController.revokeUser(new ApiKeyAuthentication(), 123L);
        assertEquals("<404 NOT_FOUND Not Found,[]>", actualRevokeUserResult.toString());
        assertEquals(HttpStatus.NOT_FOUND, actualRevokeUserResult.getStatusCode());
        assertFalse(actualRevokeUserResult.hasBody());
    }
}

