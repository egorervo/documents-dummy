package com.stripo.example.documents.filter;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@CommonsLog
public class LoggingFilter extends GenericFilterBean {
    private static final String INFO_TEMPLATE = "requestId: %s";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String requestId = getRequestId();

        request.setAttribute("requestId", requestId);
        Thread.currentThread().setName(format(INFO_TEMPLATE, requestId));

        log.info(format("(%s) %s", requestId, getPath(request)));
        try {
            chain.doFilter(request, response);
        } finally {
            Thread.currentThread().setName("");
        }
    }

    private String getPath(ServletRequest request) {
        return format("%s %s?%s",
                ((HttpServletRequest)request).getMethod(),
                ((HttpServletRequest)request).getRequestURL(),
                Optional.ofNullable(((HttpServletRequest) request).getQueryString()).orElse( ""));
    }

    private String getRequestId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
