/*
 * Copyright (C) 2021 Authlete, Inc.
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
import com.authlete.common.dto.GMRequest;
import com.authlete.common.dto.GMResponse;
import com.authlete.common.dto.GMResponse.Action;


/**
 * Handler for grant management requests.
 *
 * <p>
 * This class can be used to implement the grant management endpoint.
 * </p>
 *
 * @since 2.37
 *
 * @see <a href="https://openid.net/specs/fapi-grant-management.html"
 *      >Grant Management for OAuth 2.0</a>
 */
public class GMRequestHandler extends BaseHandler
{
    /**
     * Constructor with an implementation of {@link AuthleteApi} interface.
     *
     * @param api
     *         Implementation of {@link AuthleteApi} interface.
     */
    public GMRequestHandler(AuthleteApi api)
    {
        super(api);
    }


    /**
     * Handle a grant management request.
     *
     * @param request
     *         A grant management request.
     *
     * @return
     *         A response that should be returned from the grant management
     *         endpoint to the client application.
     *
     * @throws WebApplicationException
     */
    public Response handle(GMRequest request) throws WebApplicationException
    {
        // Call Authlete's /api/gm API.
        GMResponse response = getApiCaller().callGm(request);

        // 'action' in the response denotes the next action which
        // the implementation of grant management endpoint should take.
        Action action = response.getAction();

        // The content of the response to the client application.
        String content = response.getResponseContent();

        // Dispatch according to the action.
        switch (action)
        {
            case OK:
                // 200 OK
                return ResponseUtil.ok(content);

            case NO_CONTENT:
                // 204 No Content
                return ResponseUtil.noContent();

            case UNAUTHORIZED:
                // 401 Unauthorized
                return ResponseUtil.unauthorized(content, null);

            case FORBIDDEN:
                // 403 Forbidden
                return ResponseUtil.forbidden(content);

            case NOT_FOUND:
                // 404 Not Found
                return ResponseUtil.notFound(content);

            case CALLER_ERROR:
            case AUTHLETE_ERROR:
                // 500 Internal Server Error
                return ResponseUtil.internalServerError(content);

            default:
                // This should not happen.
                throw getApiCaller().unknownAction("/api/gm", action);
        }
    }
}
