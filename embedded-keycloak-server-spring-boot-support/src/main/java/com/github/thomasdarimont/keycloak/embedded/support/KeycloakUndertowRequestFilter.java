package com.github.thomasdarimont.keycloak.embedded.support;

import org.keycloak.common.ClientConnection;
import org.keycloak.services.filters.AbstractRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public class KeycloakUndertowRequestFilter extends AbstractRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws UnsupportedEncodingException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request.setCharacterEncoding("UTF-8");

        filter(createClientConnection(request), (session) -> {
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected ClientConnection createClientConnection(HttpServletRequest request) {
        return new UndertowClientConnection(request);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // NOOP
    }

    @Override
    public void destroy() {
        // NOOP
    }

    public static class UndertowClientConnection implements ClientConnection {

        private final HttpServletRequest request;

        public UndertowClientConnection(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public String getRemoteAddr() {
            String forwardedFor = request.getHeader("X-Forwarded-For");

            if (forwardedFor != null) {
                return forwardedFor;
            }

            return request.getRemoteAddr();
        }

        @Override
        public String getRemoteHost() {
            return request.getRemoteHost();
        }

        @Override
        public int getRemotePort() {
            return request.getRemotePort();
        }

        @Override
        public String getLocalAddr() {
            return request.getLocalAddr();
        }

        @Override
        public int getLocalPort() {
            return request.getLocalPort();
        }
    }
}
