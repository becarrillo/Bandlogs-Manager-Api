package com.api.bandlogs_manager.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.bandlogs_manager.services.BandMemberDetailsService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final BandMemberDetailsService bandMemberDetailsService;
    private final JwtUtil jwtUtil;

    @Getter
    private Claims claims = null;

    private String username = null;

    public JwtAuthenticationFilter(BandMemberDetailsService bandMemberDetailsService, JwtUtil jwtUtil) {
        this.bandMemberDetailsService = bandMemberDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (!request.getServletPath().matches("/api/v1/auth/registro|/api/v1/auth/login")) {
            final String authorizationHeader = request.getHeader("Authorization");
            String token = null;

            if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }
            if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
                final UserDetails userDetails = bandMemberDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
