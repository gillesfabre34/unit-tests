package com.airbus.retex.configuration.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ClientApiKeyFilter extends OncePerRequestFilter {

    private String header;

    public ClientApiKeyFilter(String header){
        this.header = header;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context.getAuthentication() == null){
            String apikey = request.getHeader(this.header);
            if(apikey != null){
                log.debug("Client authentication with 'Authorization' header : {}", apikey);
                ApiKeyAuthentication auth = new ApiKeyAuthentication();
                auth.setApiKey(apikey);
                context.setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);

    }
}
