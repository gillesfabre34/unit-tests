package com.airbus.retex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class FakeAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context.getAuthentication() == null){
            String fakeUser = request.getHeader(FakeAuthentication.headerUserId);
            if(fakeUser != null && !fakeUser.isEmpty()){
                log.debug("Fake authentication with: {}", fakeUser);
                FakeAuthenticationProvider.FakeUserAuth auth = new FakeAuthenticationProvider.FakeUserAuth(fakeUser);
                context.setAuthentication(auth);

                List<String> fakeRoles = Collections.list(request.getHeaders(FakeAuthentication.headerRole));
                if (!fakeRoles.isEmpty()) {
                    log.debug("Fake authentication with roles: {}", fakeRoles);
                    auth.setRoles(fakeRoles);
                }

                List<String> fakeProfileEntries = Collections.list(request.getHeaders(FakeAuthentication.headerProfileEntry));
                if (!fakeProfileEntries.isEmpty()) {
                    log.debug("Fake authentication with profile: {}", fakeProfileEntries);
                    auth.setProfile(fakeProfileEntries);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
