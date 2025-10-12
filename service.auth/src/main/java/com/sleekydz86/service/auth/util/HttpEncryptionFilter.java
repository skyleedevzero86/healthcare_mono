package com.sleekydz86.service.auth.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class HttpEncryptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        RequestDecryptWrapper requestDecryptWrapper = new RequestDecryptWrapper(httpServletRequest);
        ResponseEncryptWrapper responseEncryptWrapper = new ResponseEncryptWrapper(httpServletResponse);

        chain.doFilter(requestDecryptWrapper, responseEncryptWrapper);

        httpServletResponse.getOutputStream().write(responseEncryptWrapper.encryptResponse());
    }
}