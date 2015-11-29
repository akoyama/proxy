package com.enjoyxstudy.proxy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mortbay.util.IO;

/**
 * local file filter
 */
public class LoadLocalFileFilter implements Filter {

    /** config */
    private Config config;

    /** context */
    private ServletContext context;

    /**
     * @throws ServletException 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config_) throws ServletException {
        context = config_.getServletContext();
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        ArrayList mappingList = (ArrayList) config.getMappingMap().get(
                request.getServerName());

        String filePath = null;
        String requestURI = ((HttpServletRequest) request).getRequestURI();

        if (mappingList != null) {
            for (Iterator iterator = mappingList.iterator(); iterator.hasNext();) {
                Mapping mapping = (Mapping) iterator.next();
                if (requestURI.indexOf(mapping.getUrl()) == 0) {
                    // match
                    filePath = mapping.getFile()
                            + requestURI.substring(mapping.getUrl().length());
                    context.log("filter rewrite " + requestURI + " -> "
                            + filePath);
                    break;
                }
            }
        }

        if (filePath != null) {
            returnFile(filePath, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * @param filePath
     * @param response
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void returnFile(String filePath, ServletResponse response)
            throws FileNotFoundException, IOException {

        String mime = context.getMimeType(filePath);
        if (mime != null) {
            response.setContentType(mime);
        }

        IO.copy(new FileInputStream(filePath), response.getOutputStream());
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // none
    }

    /**
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

}
