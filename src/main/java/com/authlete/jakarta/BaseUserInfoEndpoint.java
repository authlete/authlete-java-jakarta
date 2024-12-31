/*
 * Copyright (C) 2016-2022 Authlete, Inc.
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
import jakarta.ws.rs.core.Response;
import com.authlete.common.api.AuthleteApi;
import com.authlete.common.api.Options;
import com.authlete.jakarta.UserInfoRequestHandler.Params;
import com.authlete.jakarta.spi.UserInfoRequestHandlerSpi;


/**
 * A base class for userinfo endpoints.
 *
 * @since 1.2
 *
 * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#UserInfo"
 *      >OpenID Connect Core 1.0, 5.3. UserInfo Endpoint</a>
 *
 * @author Takahiko Kawasaki
 */
public class BaseUserInfoEndpoint extends BaseResourceEndpoint
{
    /**
     * Handle a userinfo request. This method is an alias of {@link
     * #handle(AuthleteApi, UserInfoRequestHandlerSpi, UserInfoRequestHandler.Params)
     * handle}{@code (api, spi, accessToken, null, null)}.
     *
     * @param api
     *         An implementation of {@link AuthleteApi}.
     *
     * @param spi
     *         An implementation of {@link UserInfoRequestHandlerSpi}.
     *
     * @param accessToken
     *         An access token.
     *
     * @return
     *         A response that should be returned to the client application.
     */
    public Response handle(
            AuthleteApi api, UserInfoRequestHandlerSpi spi, String accessToken)
    {
        return handle(api, spi, accessToken, null, null);
    }


    /**
     * Handle a userinfo request.
     *
     * This method is an alias of the {@link #handle(AuthleteApi, UserInfoRequestHandlerSpi,
     * UserInfoRequestHandler.Params)} method.
     *
     * @param api
     *         An implementation of {@link AuthleteApi}.
     *
     * @param spi
     *         An implementation of {@link UserInfoRequestHandlerSpi}.
     *
     * @param accessToken
     *         An access token.
     *
     * @param userInfoOpts
     *         Request options for the {@code /api/auth/userinfo} API.
     *
     * @param userInfoIssueOpts
     *         Request options for the {@code /api/auth/userinfo/issue} API.
     *
     * @return
     *         A response that should be returned to the client application.
     *
     * @since 2.82
     */
    public Response handle(
            AuthleteApi api, UserInfoRequestHandlerSpi spi, String accessToken,
            Options userInfoOpts, Options userInfoIssueOpts)
    {
        Params params = new Params()
                .setAccessToken(accessToken)
                ;

        return handle(api, spi, params, userInfoOpts, userInfoIssueOpts);
    }


    /**
     * Handle a userinfo request.
     *
     * <p>
     * This method internally creates a {@link UserInfoRequestHandler} instance
     * and calls its
     * {@link UserInfoRequestHandler#handle(UserInfoRequestHandler.Params)
     * handle(Params)} method. Then, this method uses the value returned from
     * the {@code handle()} method as a response from this method.
     * </p>
     *
     * <p>
     * When {@code UserInfoRequestHandler.handle()} method raises a {@link
     * WebApplicationException}, this method calls {@link
     * #onError(WebApplicationException) onError()} method with the exception.
     * The default implementation of {@code onError()} does nothing. You can
     * override the method as necessary. After calling {@code onError()}
     * method, this method calls {@code getResponse()} method of the exception
     * and uses the returned value as a response from this method.
     * </p>
     *
     * @param api
     *         An implementation of {@link AuthleteApi}.
     *
     * @param spi
     *         An implementation of {@link UserInfoRequestHandlerSpi}.
     *
     * @param params
     *         Parameters needed to handle the userinfo request.
     *
     * @return
     *         A response that should be returned to the client application.
     *
     * @since 2.27
     */
    public Response handle(
            AuthleteApi api, UserInfoRequestHandlerSpi spi, Params params)
    {
        return handle(api, spi, params, null, null);
    }


    /**
     * Handle a userinfo request.
     *
     * <p>
     * This method internally creates a {@link UserInfoRequestHandler} instance
     * and calls its
     * {@link UserInfoRequestHandler#handle(UserInfoRequestHandler.Params)
     * handle(Params)} method. Then, this method uses the value returned from
     * the {@code handle()} method as a response from this method.
     * </p>
     *
     * <p>
     * When {@code UserInfoRequestHandler.handle()} method raises a {@link
     * WebApplicationException}, this method calls {@link
     * #onError(WebApplicationException) onError()} method with the exception.
     * The default implementation of {@code onError()} does nothing. You can
     * override the method as necessary. After calling {@code onError()}
     * method, this method calls {@code getResponse()} method of the exception
     * and uses the returned value as a response from this method.
     * </p>
     *
     * @param api
     *         An implementation of {@link AuthleteApi}.
     *
     * @param spi
     *         An implementation of {@link UserInfoRequestHandlerSpi}.
     *
     * @param params
     *         Parameters needed to handle the userinfo request.
     *
     * @param userInfoOpts
     *         Request options for the {@code /api/auth/userinfo} API.
     *
     * @param userInfoIssueOpts
     *         Request options for the {@code /api/auth/userinfo/issue} API.
     *
     * @return
     *         A response that should be returned to the client application.
     *
     * @since 2.82
     */
    public Response handle(
            AuthleteApi api, UserInfoRequestHandlerSpi spi, Params params, Options userInfoOpts,
            Options userInfoIssueOpts)
    {
        try
        {
            // Create a handler.
            UserInfoRequestHandler handler = new UserInfoRequestHandler(api, spi);

            // Delegate the task to the handler.
            return handler.handle(params, userInfoOpts, userInfoIssueOpts);
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
