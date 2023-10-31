/*
 * Copyright (C) 2015-2016 Authlete, Inc.
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


import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.authlete.common.api.AuthleteApi;


/**
 * Base class for handlers.
 *
 * @author Takahiko Kawasaki
 */
abstract class BaseHandler
{
    private final AuthleteApiCaller mApiCaller;


    protected BaseHandler(AuthleteApi api)
    {
        mApiCaller = new AuthleteApiCaller(api);
    }


    protected AuthleteApiCaller getApiCaller()
    {
        return mApiCaller;
    }


    protected InternalServerErrorException unexpected(String message, Throwable cause)
    {
        if (cause != null && cause.getMessage() != null)
        {
            // Append the message of the cause.
            message += ": " + cause.getMessage();
        }

        // Response having a response body.
        Response response = ResponseUtil.internalServerError(message, MediaType.TEXT_PLAIN_TYPE);

        return new InternalServerErrorException(message, response, cause);
    }
}
