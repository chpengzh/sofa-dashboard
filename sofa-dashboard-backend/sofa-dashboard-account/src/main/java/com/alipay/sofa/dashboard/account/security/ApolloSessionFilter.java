package com.alipay.sofa.dashboard.account.security;

import com.alipay.sofa.dashboard.account.dao.api.SessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Component
public class ApolloSessionFilter implements Filter {

    private final SessionRepository sessionRepo;

    ApolloSessionFilter(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing...
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Set session id into current thread context
        Cookie[] cookies = Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]);
        Stream.of(cookies)
            .filter(it -> Objects.equals(it.getName(), "JSESSIONID"))
            .findAny()
            .ifPresent(sessionCookie -> sessionRepo.setCurrentSessionId(sessionCookie.getValue()));

        try {
            chain.doFilter(req, res);
        } finally {
            sessionRepo.setCurrentSessionId(null);
        }
    }

    @Override
    public void destroy() {
        // Do nothing...
    }
}
