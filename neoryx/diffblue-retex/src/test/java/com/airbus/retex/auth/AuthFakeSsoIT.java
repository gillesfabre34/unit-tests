package com.airbus.retex.auth;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.FakeAuthentication;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.admin.UserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthFakeSsoIT extends BaseControllerTest {

    public static final String OPERATOR_READ_FORBIDDEN_ENDPOINT = API + "/users";
    public static final String OPERATOR_READ_OK_ENDPOINT = API + "/filterings";

    public static final String ADMINISTRATOR_ROLE = "administrator";
    public static final String OPERATOR_ROLE = "internal_operator";
    public static final String INVALID_ROLE = "INVALID";

    @Autowired
    private UserRepository userRepository;

    private String withEmail;
    private Matcher expectedError;
    private Set<String> profileEntries;
    private Set<String> roles;

    @BeforeEach
    public void beforeEach() {
        withRequest = get(OPERATOR_READ_FORBIDDEN_ENDPOINT);
        withEmail = null;
        expectedError = null;
        profileEntries = null;
        roles = null;
    }

    @Test
    public void tests() {
        assertNotNull(dataset);
    }

    @Test
    public void authUserWithoutUpdate() throws Exception {
        withEmail = dataset.user_superAdmin.getEmail();
        expectedStatus = HttpStatus.OK;
        check();
    }

    @Test
    public void authUserWithRoleUpdate() throws Exception {
        withEmail = dataset.user_superAdmin.getEmail();
        expectedStatus = HttpStatus.OK;
        //Attencheck();

        addRole(OPERATOR_ROLE);
        expectedStatus = HttpStatus.FORBIDDEN;
        check();

        unsetRoles();
        expectedStatus = HttpStatus.FORBIDDEN;
        check();

        withRequest = get(OPERATOR_READ_OK_ENDPOINT);
        expectedStatus = HttpStatus.OK;
        check();

        withRequest = get(OPERATOR_READ_FORBIDDEN_ENDPOINT);
        addRole(ADMINISTRATOR_ROLE);
        expectedStatus = HttpStatus.OK;
        check();
    }

    @Test
    public void authUserWithProfileUpdateStaffNumber() throws Exception {
        withEmail = dataset.user_superAdmin.getEmail();
        String staffNumber = "MODIFIED";
        // At least a role must be present in the request to trigger the user update using fakeAuthentication
        addRole(ADMINISTRATOR_ROLE);
        addProfileEntry("staffNumber", staffNumber);
        expectedStatus = HttpStatus.OK;
        check();

        User user = userRepository.findByEmail(withEmail).orElseThrow();
        assertThat(user.getStaffNumber(), equalTo(staffNumber));
        assertThat(user.getFirstName(), equalTo(dataset.user_superAdmin.getFirstName()));
        assertThat(user.getLastName(), equalTo(dataset.user_superAdmin.getLastName()));
    }

    @Test
    public void authNewUserWithoutRole() throws Exception {
        withEmail = "newuser@exmaple.com";
        expectedStatus = HttpStatus.UNAUTHORIZED;
        expectedError = equalTo("FakeAuthenticationException: User not found");
        check();
    }

    @Test
    public void authNewUserWithRoleAndIncompleteProfile() throws Exception {
        withEmail = "newuser@exmaple.com";
        addRole(ADMINISTRATOR_ROLE);
        expectedStatus = HttpStatus.UNAUTHORIZED;
        expectedError = startsWith("FakeAuthenticationException: Missing fields:");
        check();
    }

    @Test
    public void authNewUserWithRoleAndProfile() throws Exception {
        withEmail = "newuser@exmaple.com";
        String staffNumber = "X123456";
        String firstName = "fn";
        String lastName = "ln";

        addRole(ADMINISTRATOR_ROLE);
        addProfileEntry("staffNumber", staffNumber);
        addProfileEntry("firstName", firstName);
        addProfileEntry("lastName", lastName);
        expectedStatus = HttpStatus.OK;
        check();

        User user = userRepository.findByEmail(withEmail).orElseThrow();
        assertThat(user.getStaffNumber(), equalTo(staffNumber));
        assertThat(user.getFirstName(), equalTo(firstName));
        assertThat(user.getLastName(), equalTo(lastName));
    }

    private void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<String>();
        }
        roles.add(role);
    }

    private void unsetRoles() {
        roles = null;
    }

    private void addProfileEntry(String name,String value) {
        if (profileEntries == null) {
            profileEntries = new HashSet<String>();
        }
        profileEntries.add(name + ":" + value);
    }

    private ResultActions check() throws Exception {
        withRequest
                .contentType(MediaType.APPLICATION_JSON)
                .locale(Locale.ENGLISH)
                .params(withParams)
                .header(FakeAuthentication.headerUserId, withEmail == null ? "" : withEmail);

        if (roles != null) {
            roles.forEach(r -> withRequest.header(FakeAuthentication.headerRole, r));
        }

        if (profileEntries != null) {
            profileEntries.forEach(p -> withRequest.header(FakeAuthentication.headerProfileEntry, p));
        }

        ResultActions result = mockMvc.perform(withRequest);
        result.andExpect(status().is(expectedStatus.value()));

        if (checkResponseContentType) {
            result.andExpect(content().contentTypeCompatibleWith(mediatype));
        }

        if (expectedError != null) {
            result.andExpect(jsonPath("$.error_description").value(expectedError));
        }

        return result;
    }
}
