/*
 * Copyright (C) 2019 Authlete, Inc.
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
package com.authlete.jakarta;


import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import com.authlete.common.api.AuthleteApi;


/**
 * A base class for pushed authorization endpoints.
 *
 * @since 2.21
 *
 * @see <a href="https://tools.ietf.org/html/draft-lodderstedt-oauth-par"
 *      >OAuth 2.0 Pushed Authorization Requests</a>
 *
 * @author Justin Richer
 */
public class BasePushedAuthReqEndpoint extends BaseEndpoint
{
    /**
     * Handle a pushed authorization request.
     *
     * <p>
     * This method internally creates a {@link PushedAuthReqHandler} instance
     * and calls its {@link PushedAuthReqHandler#handle(MultivaluedMap, String, String[])}
     * method. Then, this method uses the value returned from the {@code handle()}
     * method as a response from this method.
     * </p>
     *
     * <p>
     * When {@code PushedAuthReqHandler.handle()} method raises a {@link
     * WebApplicationException}, this method calls {@link #onError(WebApplicationException)
     * onError()} method with the exception. The default implementation of {@code onError()}
     * does nothing. You can override the method as necessary. After calling
     * {@code onError()} method, this method calls {@code getResponse()} method of
     * the exception and uses the returned value as a response from this method.
     * </p>
     *
     * @param api
     *            An implementation of {@link AuthleteApi}.
     *
     * @param parameters
     *            Request parameters of the pushed authorization request.
     *
     * @param authorization
     *            The value of {@code Authorization} header of the pushed authorization request.
     *
     * @param clientCertificates
     *            The certificate path used in mutual TLS authentication, in PEM format. The
     *            client's own certificate is the first in this array. Can be {@code null}.
     *
     * @return
     *         A response that should be returned to the client application.
     */
    protected Response handle(
            AuthleteApi api, MultivaluedMap<String, String> parameters,
            String authorization, String[] clientCertificates)
    {
        try
        {
            // Create a handler.
            PushedAuthReqHandler handler = new PushedAuthReqHandler(api);

            // Delegate the task to the handler.
            return handler.handle(parameters, authorization, clientCertificates);
        }
        catch (WebApplicationException e)
        {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
        }
    }
}
