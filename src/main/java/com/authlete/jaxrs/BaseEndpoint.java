/*
 * Copyright (C) 2016 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package com.authlete.jaxrs;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;


/**
 * A base class for endpoints.
 *
 * @since 1.2
 *
 * @author Takahiko Kawasaki
 */
public class BaseEndpoint
{
    
    private HeaderClientCertificateExtractor headerExtractor;
    private HttpsRequestClientCertificateExtractor directExtractor;

    /**
     * Called when the internal request handler raises an exception.
     * The default implementation of this method calls {@code
     * printStackTrace()} of the given exception instance and does
     * nothing else. Override this method as necessary.
     *
     * @param exception
     *         An exception thrown by the internal request handler.
     */
    protected void onError(WebApplicationException exception)
    {
        exception.printStackTrace();
    }

    /**
     * Utility method for extracting a single client certificate from the default
     * certificate extractors. First checks the request itself for an attached
     * certificate using {@link javax.servlet.request.X509Certificate}, then
     * checks the incoming request headers for reverse-proxied certificates
     * using default headers.
     * 
     * @see ClientCertificateExtractor
     * 
     * @see 
     * 
     * @param request
     *          The incoming HTTP request to search for the client's certificate
     *          
     * @return
     *          The client's mutual TLS certificate.
     */
    protected String[] extractClientCertificateChain(HttpServletRequest request)
    {
        String[] chain = getHttpsRequestClientCertificateExtractor().extractClientCertificateChain(request);
        
        if (chain == null || chain.length < 1)
        {
            return getHeaderClientCertificateExtractor().extractClientCertificateChain(request);
        }
        else
        {
            return chain;
        }
    }

    private ClientCertificateExtractor getHttpsRequestClientCertificateExtractor()
    {
        if (directExtractor == null)
        {
            directExtractor = new HttpsRequestClientCertificateExtractor();
        }
        return directExtractor;
    }
    
    private ClientCertificateExtractor getHeaderClientCertificateExtractor()
    {
        if (headerExtractor == null)
        {
            headerExtractor = new HeaderClientCertificateExtractor();
        }
        return headerExtractor;
    }

    /**
     * Utility method for extracting a single client certificate from the default
     * certificate extractors. Calls extractClientCertificateChain and returns the
     * first entry in the array, if any, null otherwise.
     * 
     * @param request
     *          The incoming HTTP request to search for the client's certificate
     *          
     * @return
     *          The client's mutual TLS certificate.
     */
    protected String extractClientCertificate(HttpServletRequest request)
    {
        String[] certs = extractClientCertificateChain(request);
        
        if (certs != null && certs.length > 0)
        {
            return certs[0];
        }
        else
        {
            return null;
        }
    }
    
}
