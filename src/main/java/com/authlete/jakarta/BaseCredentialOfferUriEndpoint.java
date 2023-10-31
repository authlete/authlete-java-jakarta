/*
 * Copyright (C) 2016-2023 Authlete, Inc.
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
import com.authlete.common.dto.CredentialOfferInfoRequest;


public class BaseCredentialOfferUriEndpoint extends BaseEndpoint
{
    public Response handle(final AuthleteApi api, final CredentialOfferInfoRequest request)
    {
        try
        {
            // Create a handler.
            CredentialOfferUriRequestHandler handler = new CredentialOfferUriRequestHandler(api);

            // Delegate the task to the handler.
            return handler.handle(request);
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
