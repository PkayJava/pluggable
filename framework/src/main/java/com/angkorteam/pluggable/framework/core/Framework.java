package com.angkorteam.pluggable.framework.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.WicketFilter;

/**
 * @author Socheat KHAUV
 */
public class Framework extends WicketFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest((HttpServletRequest) request);
        super.doFilter(multiReadRequest, response, chain);
    }

}
