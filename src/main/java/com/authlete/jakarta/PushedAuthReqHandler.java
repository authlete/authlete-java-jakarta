/*
 * Copyright (C) 2019-2025 Authlete, Inc.
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


import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import com.authlete.common.api.AuthleteApi;
import com.authlete.common.api.Options;
import com.authlete.common.dto.PushedAuthReqResponse;
import com.authlete.common.dto.PushedAuthReqResponse.Action;
import com.authlete.common.web.BasicCredentials;


/**
 * Handler for pushed authorization request endpoint requests.
 *
 * <p>
 * In an implementation of the pushed authorization request endpoint, call
 * {@link #handle(MultivaluedMap, String, String[]) handle()} method and use
 * the response as the response from the endpoint to the client application.
 * The {@code handle()} method calls Authlete's
 * {@code /api/pushed_auth_req} API, receives a response from the API, and
 * dispatches processing according to the {@code action} parameter in the response.
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/draft-lodderstedt-oauth-par"
 *      >OAuth 2.0 Pushed Authorization Requests</a>
 *
 * @since 2.21
 *
 * @author Justin Richer
 */
public class PushedAuthReqHandler extends BaseHandler
{
    /**
     * Parameters passed to the {@link PushedAuthReqHandler#handle(Params)}
     * method.
     *
     * @since 2.69
     */
    public static class Params implements Serializable
    {
        private static final long serialVersionUID = 1L;


        private MultivaluedMap<String, String> parameters;
        private String authorization;
        private String[] clientCertificatePath;
        private String dpop;
        private String htm;
        private String htu;


        /**
         * Get the request parameters of the PAR request.
         *
         * @return
         *         The request parameters of the PAR request.
         */
        public MultivaluedMap<String, String> getParameters()
        {
            return parameters;
        }


        /**
         * Set the request parameters of the PAR request.
         *
         * @param parameters
         *         The request parameters of the PAR request.
         *
         * @return
         *         {@code this} object.
         */
        public Params setParameters(MultivaluedMap<String, String> parameters)
        {
            this.parameters = parameters;

            return this;
        }


        /**
         * Get the value of the {@code Authorization} header in the PAR request.
         * A pair of client ID and client secret is embedded there when the
         * client authentication method is {@code client_secret_basic}.
         *
         * @return
         *         The value of the {@code Authorization} header.
         */
        public String getAuthorization()
        {
            return authorization;
        }


        /**
         * Set the value of the {@code Authorization} header in the PAR request.
         * A pair of client ID and client secret is embedded there when the
         * client authentication method is {@code client_secret_basic}.
         *
         * @param authorization
         *         The value of the {@code Authorization} header.
         *
         * @return
         *         {@code this} object.
         */
        public Params setAuthorization(String authorization)
        {
            this.authorization = authorization;

            return this;
        }


        /**
         * Get the path of the client's certificate, each in PEM format.
         * The first item in the array is the client's certificate itself.
         *
         * @return
         *         The path of the client's certificate.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc8705.html"
         *      >RFC 8705 : OAuth 2.0 Mutual-TLS Client Authentication and
         *       Certificate-Bound Access Tokens</a>
         */
        public String[] getClientCertificatePath()
        {
            return clientCertificatePath;
        }


        /**
         * Set the path of the client's certificate, each in PEM format.
         * The first item in the array is the client's certificate itself.
         *
         * @param path
         *         The path of the client's certificate.
         *
         * @return
         *         {@code this} object.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc8705.html"
         *      >RFC 8705 : OAuth 2.0 Mutual-TLS Client Authentication and
         *       Certificate-Bound Access Tokens</a>
         */
        public Params setClientCertificatePath(String[] path)
        {
            this.clientCertificatePath = path;

            return this;
        }


        /**
         * Get the DPoP proof JWT (the value of the {@code DPoP} HTTP header).
         *
         * @return
         *         The DPoP proof JWT.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public String getDpop()
        {
            return dpop;
        }


        /**
         * Set the DPoP proof JWT (the value of the {@code DPoP} HTTP header).
         *
         * @param dpop
         *         The DPoP proof JWT.
         *
         * @return
         *         {@code this} object.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public Params setDpop(String dpop)
        {
            this.dpop = dpop;

            return this;
        }


        /**
         * Get the HTTP method of the PAR request.
         *
         * @return
         *         The HTTP method of the PAR request.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public String getHtm()
        {
            return htm;
        }


        /**
         * Set the HTTP method of the PAR request.
         *
         * <p>
         * The value should be {@code "POST"} unless new specifications
         * allowing other HTTP methods at the PAR endpoint are developed.
         * If this parameter is omitted, {@code "POST"} is used as the
         * default value.
         * </p>
         *
         * <p>
         * The value passed here will be used to validate the DPoP proof JWT.
         * </p>
         *
         * @param htm
         *         The HTTP method of the PAR request.
         *
         * @return
         *         {@code this} object.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public Params setHtm(String htm)
        {
            this.htm = htm;

            return this;
        }


        /**
         * Get the URL of the PAR endpoint.
         *
         * @return
         *         The URL of the PAR endpoint.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public String getHtu()
        {
            return htu;
        }


        /**
         * Set the URL of the PAR endpoint.
         *
         * <p>
         * If this parameter is omitted, the {@code pushedAuthReqEndpoint}
         * property of {@link Service} will be used as the default value.
         * </p>
         *
         * <p>
         * The value passed here will be used to validate the DPoP proof JWT.
         * </p>
         *
         * @param htu
         *         The URL of the PAR endpoint.
         *
         * @return
         *         {@code this} object.
         *
         * @see <a href="https://www.rfc-editor.org/rfc/rfc9449.html"
         *      >RFC 9449: OAuth 2.0 Demonstrating Proof of Possession (DPoP)</a>
         */
        public Params setHtu(String htu)
        {
            this.htu = htu;

            return this;
        }
    }


    /**
     * Constructor with an implementation of {@link AuthleteApi} interface.
     *
     * @param api
     *            Implementation of {@link AuthleteApi} interface.
     */
    public PushedAuthReqHandler(AuthleteApi api)
    {
        super(api);
    }


    /**
     * Handle a pushed authorization request. This method is an alias
     * of {@link #handle(MultivaluedMap, String, String[], Options) handle}{@code
     * (parameters, authorization, clientCertificatePath, null)}.
     *
     * @param parameters
     *            Request parameters of a pushed authorization request.
     *
     * @param authorization
     *            The value of {@code Authorization} header in the pushed
     *            authorization request. A client application may embed its
     *            pair of client ID and client secret in a pushed authorization
     *            request using <a href="https://tools.ietf.org/html/rfc2617#section-2"
     *            >Basic Authentication</a>.
     *
     * @param clientCertificatePath
     *            The path of the client's certificate, each in PEM format.
     *            The first item in the array is the client's certificate itself.
     *            May be {@code null} if the client did not send a certificate or path.
     *
     * @return
     *         A response that should be returned from the endpoint to the
     *         client application.
     *
     * @throws WebApplicationException
     *             An error occurred.
     */
    public Response handle(
            MultivaluedMap<String, String> parameters, String authorization,
            String[] clientCertificatePath) throws WebApplicationException
    {
        return handle(parameters, authorization, clientCertificatePath, null);
    }


    /**
     * Handle a pushed authorization request. This method is an alias of the {@link
     * #handle(Params, Options)} method.
     *
     * @param parameters
     *            Request parameters of a pushed authorization request.
     *
     * @param authorization
     *            The value of {@code Authorization} header in the pushed
     *            authorization request. A client application may embed its
     *            pair of client ID and client secret in a pushed authorization
     *            request using <a href="https://tools.ietf.org/html/rfc2617#section-2"
     *            >Basic Authentication</a>.
     *
     * @param clientCertificatePath
     *            The path of the client's certificate, each in PEM format.
     *            The first item in the array is the client's certificate itself.
     *            May be {@code null} if the client did not send a certificate or path.
     *
     * @param options
     *         Request options for the {@code /api/pushed_auth_req} API.
     *
     * @return
     *         A response that should be returned from the endpoint to the
     *         client application.
     *
     * @throws WebApplicationException
     *             An error occurred.
     *
     * @since 2.82
     */
    public Response handle(
            MultivaluedMap<String, String> parameters, String authorization,
            String[] clientCertificatePath, Options options) throws WebApplicationException
    {
        Params params = new Params()
                .setParameters(parameters)
                .setAuthorization(authorization)
                .setClientCertificatePath(clientCertificatePath)
                ;

        return handle(params, options);
    }


    /**
     * Handle a PAR request. This method is an alias of {@link #handle(Params, Options)
     * handle}{@code (params, null)}.
     *
     * @param params
     *         Parameters needed to handle the PAR request.
     *         Must not be {@code null}.
     *
     * @return
     *         A response that should be returned from the endpoint to the
     *         client application.
     *
     * @throws WebApplicationException
     *         An error occurred.
     *
     * @since 2.69
     */
    public Response handle(Params params)
    {
        return handle(params, null);
    }


    /**
     * Handle a PAR request.
     *
     * @param params
     *         Parameters needed to handle the PAR request.
     *         Must not be {@code null}.
     *
     * @param options
     *         Request options for the {@code /api/pushed_auth_req} API.
     *
     * @return
     *         A response that should be returned from the endpoint to the
     *         client application.
     *
     * @throws WebApplicationException
     *         An error occurred.
     *
     * @since 2.82
     */
    public Response handle(Params params, Options options)
    {
        // Convert the value of Authorization header (credentials of
        // the client application), if any, into BasicCredentials.
        BasicCredentials credentials = BasicCredentials.parse(params.getAuthorization());

        // The credentials of the client application extracted from
        // 'Authorization' header. These may be null.
        String clientId     = credentials == null ? null : credentials.getUserId();
        String clientSecret = credentials == null ? null : credentials.getPassword();

        try
        {
            // Process the given parameters.
            return process(
                    params.getParameters(),
                    clientId,
                    clientSecret,
                    params.getClientCertificatePath(),
                    params.getDpop(),
                    params.getHtm(),
                    params.getHtu(),
                    options
                    );
        }
        catch (WebApplicationException e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            // Unexpected error.
            throw unexpected("Unexpected error in PushedAuthReqHandler", t);
        }
    }


    /**
     * Process the parameters of the pushed authorization request.
     */
    private Response process(
            MultivaluedMap<String, String> parameters, String clientId,
            String clientSecret, String[] clientCertificatePath,
            String dpop, String htm, String htu, Options options)
    {
        String clientCertificate = null;
        if (clientCertificatePath != null && clientCertificatePath.length > 0)
        {
            // The first one is the client's certificate.
            clientCertificate = clientCertificatePath[0];

            // if we have more in the path, pass them along separately without the first one
            if (clientCertificatePath.length > 1)
            {
                clientCertificatePath = Arrays.copyOfRange(
                        clientCertificatePath, 1, clientCertificatePath.length);
            }
        }

        PushedAuthReqResponse response = getApiCaller().callPushedAuthReq(
                parameters, clientId, clientSecret,
                clientCertificate, clientCertificatePath, dpop, htm, htu, options);

        // 'action' in the response denotes the next action which
        // this service implementation should take.
        Action action = response.getAction();

        // The content of the response to the client application.
        String content = response.getResponseContent();

        // Additional HTTP headers.
        Map<String, Object> headers = prepareHeaders(response);

        // Dispatch according to the action.
        switch (action)
        {
            case BAD_REQUEST:
                // 400 Bad Request
                return ResponseUtil.badRequest(content, headers);

            case CREATED:
                // 201 Created
                return ResponseUtil.created(content, headers);

            case FORBIDDEN:
                // 403 forbidden
                return ResponseUtil.forbidden(content, headers);

            case INTERNAL_SERVER_ERROR:
                // 500 Internal Server Error
                return ResponseUtil.internalServerError(content, headers);

            case PAYLOAD_TOO_LARGE:
                // 413 Too Large
                return ResponseUtil.tooLarge(content, headers);

            case UNAUTHORIZED:
                // 401 Unauthorized
                return ResponseUtil.unauthorized(content, null, headers);

            default:
                // This never happens.
                throw getApiCaller().unknownAction("/api/pushed_auth_req", action);
        }
    }


    private static Map<String, Object> prepareHeaders(PushedAuthReqResponse response)
    {
        Map<String, Object> headers = new LinkedHashMap<>();

        // DPoP-Nonce
        String dpopNonce = response.getDpopNonce();
        if (dpopNonce != null)
        {
            headers.put("DPoP-Nonce", dpopNonce);
        }

        return headers;
    }
}
