/*
 * Copyright (C) 2015-2022 Authlete, Inc.
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
package com.authlete.jakarta.spi;


import jakarta.ws.rs.core.Response;
import com.authlete.common.dto.Property;
import com.authlete.common.dto.TokenResponse;
import com.authlete.jakarta.TokenRequestHandler;


/**
 * Service Provider Interface to work with {@link
 * com.authlete.jakarta.TokenRequestHandler TokenRequestHandler}.
 *
 * <p>
 * An implementation of this interface must be given to the constructor
 * of {@link com.authlete.jakarta.TokenRequestHandler TokenRequestHandler}
 * class.
 * </p>
 *
 * @author Takahiko Kawasaki
 */
public interface TokenRequestHandlerSpi
{
    /**
     * Authenticate an end-user.
     *
     * <p>
     * This method is called only when <a href=
     * "https://tools.ietf.org/html/rfc6749#section-4.3">Resource Owner
     * Password Credentials Grant</a> was used. Therefore, if you have
     * no mind to support Resource Owner Password Credentials, always
     * return {@code null}. In typical cases, you don't have to support
     * Resource Owner Password Credentials Grant.
     * FYI: RFC 6749 says <i>"The authorization server should take special
     * care when enabling this grant type and only allow it when other
     * flows are not viable."</i>
     * </p>
     *
     * <p>
     * Below is an example implementation using <a href=
     * "http://shiro.apache.org/">Apache Shiro</a>.
     * </p>
     *
     * <blockquote>
     * <pre style="border: 1px solid gray; padding: 0.5em; margin: 1em;">
     * <span style="color: gray;">&#x40;Override</span>
     * <span style="color: purple; font-weight: bold;">public</span> String authenticateUser(String username, String password)
     * {
     *     <span style="color: green;">// Pack the username and password into AuthenticationToken
     *     // which Apache Shiro's SecurityManager can accept.</span>
     *     <a href="https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authc/AuthenticationToken.html"
     *     style="text-decoration: none;">AuthenticationToken</a> credentials =
     *         <span style="color: purple; font-weight: bold;">new</span> <a href=
     *         "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authc/UsernamePasswordToken.html#UsernamePasswordToken(java.lang.String,%20java.lang.String)"
     *         style="text-decoration: none;">UsernamePasswordToken</a>(username, password);
     *
     *     <span style="color: purple; font-weight: bold;">try</span>
     *     {
     *         <span style="color: green;">// Authenticate the resource owner.</span>
     *         <a href="https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authz/AuthorizationInfo.html"
     *         style="text-decoration: none;">AuthenticationInfo</a> info =
     *             <a href="https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/SecurityUtils.html"
     *             style="text-decoration: none;">SecurityUtils</a>.<a href=
     *             "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/SecurityUtils.html#getSecurityManager()"
     *             style="text-decoration: none;">getSecurityManager()</a>.<a href=
     *             "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authc/Authenticator.html#authenticate(org.apache.shiro.authc.AuthenticationToken)"
     *             style="text-decoration: none;">authenticate</a>(credentials);
     *
     *         <span style="color: green;">// Get the subject of the authenticated user.</span>
     *         <span style="color: purple; font-weight: bold;">return</span> info.<a href=
     *         "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authc/AuthenticationInfo.html#getPrincipals()"
     *         style="text-decoration: none;">getPrincipals()</a>.<a href=
     *         "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/subject/PrincipalCollection.html#getPrimaryPrincipal()"
     *         style="text-decoration: none;">getPrimaryPrincipal()</a>.toString();
     *     }
     *     <span style="color: purple; font-weight: bold;">catch</span> (<a href=
     *     "https://shiro.apache.org/static/1.2.3/apidocs/org/apache/shiro/authz/AuthorizationException.html"
     *     style="text-decoration: none;">AuthenticationException</a> e)
     *     {
     *         <span style="color: green;">// Not authenticated.</span>
     *         <span style="color: purple; font-weight: bold;">return</span> null;
     *     }
     * }</pre>
     * </blockquote>
     *
     * @param username
     *         The value of {@code username} parameter in the token request.
     *
     * @param password
     *         The value of {@code password} parameter in the token request.
     *
     * @return
     *         The subject (= unique identifier) of the authenticated
     *         end-user. If the pair of {@code username} and {@code
     *         password} is invalid, {@code null} should be returned.
     */
    String authenticateUser(String username, String password);


    /**
     * Get extra properties to associate with an access token.
     *
     * <p>
     * This method is expected to return an array of extra properties.
     * The following is an example that returns an array containing one
     * extra property.
     * </p>
     *
     * <pre style="border: 1px solid gray; padding: 0.5em; margin: 1em;">
     * <span style="color: gray;">&#x40;Override</span>
     * <span style="color: purple; font-weight: bold;">public</span> {@link Property}[] getProperties()
     * {
     *     <span style="color: purple; font-weight: bold;">return</span> <span style="color: purple; font-weight: bold;">new</span> {@link Property}[] {
     *         <span style="color: purple; font-weight: bold;">new</span> {@link Property#Property(String, String)
     *     Property}(<span style="color: darkred;">"example_parameter"</span>, <span style="color: darkred;">"example_value"</span>)
     *     };
     * }</pre>
     * </blockquote>
     *
     * <p>
     * Extra properties returned from this method will appear as top-level entries
     * in a JSON response from an authorization server as shown in <a href=
     * "https://tools.ietf.org/html/rfc6749#section-5.1">5.1. Successful Response</a>
     * in RFC 6749.
     * </p>
     *
     * <p>
     * Keys listed below should not be used and they would be ignored on
     * the server side even if they were used. It's because they are reserved
     * in <a href="https://tools.ietf.org/html/rfc6749">RFC 6749</a> and
     * <a href="http://openid.net/specs/openid-connect-core-1_0.html"
     * >OpenID Connect Core 1.0</a>.
     * </p>
     *
     * <ul>
     *   <li>{@code access_token}
     *   <li>{@code token_type}
     *   <li>{@code expires_in}
     *   <li>{@code refresh_token}
     *   <li>{@code scope}
     *   <li>{@code error}
     *   <li>{@code error_description}
     *   <li>{@code error_uri}
     *   <li>{@code id_token}
     * </ul>
     *
     * <p>
     * Note that <b>there is an upper limit on the total size of extra properties</b>.
     * On the server side, the properties will be (1) converted to a multidimensional
     * string array, (2) converted to JSON, (3) encrypted by AES/CBC/PKCS5Padding, (4)
     * encoded by base64url, and then stored into the database. The length of the
     * resultant string must not exceed 65,535 in bytes. This is the upper limit, but
     * we think it is big enough.
     * </p>
     *
     * <p>
     * When the value of {@code grant_type} parameter contained in the token request
     * from the client application is {@code authorization_code} or {@code refresh_token},
     * extra properties are merged. Rules are as described in the table below.
     * </p>
     *
     * <blockquote>
     * <table border="1" cellpadding="5" style="border-collapse: collapse;">
     *   <thead>
     *     <tr>
     *       <th><code>grant_type</code></th>
     *       <th>Description</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td><code>authorization_code</code></td>
     *       <td>
     *         <p>
     *           If the authorization code presented by the client application already
     *           has extra properties (this happens if {@link
     *           AuthorizationDecisionHandlerSpi#getProperties()} returned extra properties
     *           when the authorization code was issued), extra properties returned by this
     *           method will be merged into the existing extra properties. Note that the
     *           existing extra properties will be overwritten if extra properties returned
     *           by this method have the same keys.
     *         </p>
     *         <p>
     *           For example, if an authorization code has two extra properties, {@code a=1}
     *           and {@code b=2}, and if this method returns two extra properties, {@code a=A}
     *           and {@code c=3}, the resultant access token will have three extra properties,
     *           {@code a=A}, {@code b=2} and {@code c=3}.
     *         </p>
     *       </td>
     *     </tr>
     *     <tr>
     *       <td><code>refresh_token</code></td>
     *       <td>
     *         <p>
     *           If the access token associated with the refresh token presented by the
     *           client application already has extra properties, extra properties returned
     *           by this method will be merged into the existing extra properties. Note that
     *           the existing extra properties will be overwritten if extra properties
     *           returned by this method have the same keys.
     *         </p>
     *       </td>
     *   </tbody>
     * </table>
     * </blockquote>
     *
     * @return
     *         Extra properties. If {@code null} is returned, any extra
     *         property will not be associated.
     *
     * @since 1.3
     */
    Property[] getProperties();


    /**
     * Handle a token exchange request.
     *
     * <p>
     * This method is called when the grant type of the token request is
     * {@code "urn:ietf:params:oauth:grant-type:token-exchange"}. The grant
     * type is defined in <a href="https://www.rfc-editor.org/rfc/rfc8693.html"
     * >RFC 8693: OAuth 2.0 Token Exchange</a>.
     * </p>
     *
     * <p>
     * RFC 8693 is very flexible. In other words, the specification does not
     * define details that are necessary for secure token exchange. Therefore,
     * implementations have to complement the specification with their own
     * rules.
     * </p>
     *
     * <p>
     * The argument passed to this method is an instance of {@link TokenResponse}
     * that represents a response from Authlete's {@code /auth/token} API. The
     * instance contains information about the token exchange request such as
     * the value of the {@code subject_token} request parameter. Implementations
     * of this {@code tokenExchange} method are supposed to (1) validate the
     * information based on their own rules, (2) generate a token (e.g. an access
     * token) using the information, and (3) prepare a token response in the JSON
     * format that conforms to <a href=
     * "https://www.rfc-editor.org/rfc/rfc8693.html#section-2.2">Section 2.2</a>
     * of RFC 8693.
     * </p>
     *
     * <p>
     * Authlete's {@code /auth/token} API performs validation of token exchange
     * requests to some extent. Therefore, authorization server implementations
     * don't have to repeat the same validation steps. See the <a href=
     * "https://authlete.github.io/authlete-java-common/">JavaDoc</a> of the
     * {@link TokenResponse} class for details about the validation steps.
     * </p>
     *
     * <p>
     * NOTE: Token Exchange is supported by Authlete 2.3 and newer versions. If
     * the Authlete server of your system is older than version 2.3, the grant
     * type ({@code "urn:ietf:params:oauth:grant-type:token-exchange"}) is not
     * supported and so this method is never called.
     * </p>
     *
     * @param tokenResponse
     *         A response from Authlete's {@code /auth/token} API.
     *
     * @return
     *         A response from the token endpoint. It must conform to <a href=
     *         "https://www.rfc-editor.org/rfc/rfc8693.html#section-2.2">Section
     *         2.2</a> of RFC 8693. If this method returns {@code null},
     *         {@link TokenRequestHandler} will generate {@code 400 Bad Request}
     *         with <code>{"error":"unsupported_grant_type"}</code>.
     *
     * @since 2.47
     * @since Authlete 2.3
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc8693.html"
     *      >RFC 8693 OAuth 2.0 Token Exchange</a>
     */
    Response tokenExchange(TokenResponse tokenResponse);


    /**
     * Handle a token request that uses the grant type
     * {@code "urn:ietf:params:oauth:grant-type:jwt-bearer"} (<a href=
     * "https://www.rfc-editor.org/rfc/rfc7523.html">RFC 7523</a>).
     *
     * <p>
     * This method is called when the grant type of the token request is
     * {@code "urn:ietf:params:oauth:grant-type:jwt-bearer"}. The grant type
     * is defined in <a href="https://www.rfc-editor.org/rfc/rfc7523.html"
     * >RFC 7523: JSON Web Token (JWT) Profile for OAuth 2.0 Client
     * Authentication and Authorization Grants</a>.
     * </p>
     *
     * <p>
     * The grant type utilizes a JWT as an authorization grant, but the
     * specification does not define details about how the JWT is generated
     * by whom. As a result, it is not defined in the specification how to
     * obtain the key whereby to verify the signature of the JWT. Therefore,
     * each deployment has to define their own rules which are necessary to
     * determine the key for signature verification.
     * </p>
     *
     * <p>
     * The argument passed to this method is an instance of {@link TokenResponse}
     * that represents a response from Authlete's {@code /auth/token} API. The
     * instance contains information about the token request such as the value
     * of the {@code assertion} request parameter. Implementations of this
     * {@code jwtBearer} method are supposed to (1) validate the authorization
     * grant (= the JWT specified by the {@code assertion} request parameter),
     * (2) generate an access token, and (3) prepare a token response in the
     * JSON format that conforms to <a href=
     * "https://www.rfc-editor.org/rfc/rfc6749.html">RFC 6749</a>.
     * </p>
     *
     * <p>
     * Authlete's {@code /auth/token} API performs validation of token requests
     * to some extent. Therefore, authorization server implementations don't
     * have to repeat the same validation steps. Basically, what implementations
     * have to do is to verify the signature of the JWT. See the <a href=
     * "https://authlete.github.io/authlete-java-common/">JavaDoc</a> of the
     * {@link TokenResponse} class for details about the validation steps.
     * </p>
     *
     * <p>
     * NOTE: JWT Authorization Grant is supported by Authlete 2.3 and newer
     * versions. If the Authlete server of your system is older than version
     * 2.3, the grant type ({@code "urn:ietf:params:oauth:grant-type:jwt-bearer"})
     * is not supported and so this method is never called.
     * </p>
     *
     * @param tokenResponse
     *         A response from Authlete's {@code /auth/token} API.
     *
     * @return
     *         A response from the token endpoint. It must conform to <a href=
     *         "https://www.rfc-editor.org/rfc/rfc6749.html">RFC 6749</a>. If
     *         this method returns {@code null}, {@link TokenRequestHandler}
     *         will generate {@code 400 Bad Request} with
     *         <code>{"error":"unsupported_grant_type"}</code>.
     *
     * @since 2.48
     * @since Authlete 2.3
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7521.html">RFC 7521
     *      Assertion Framework for OAuth 2.0 Client Authentication and
     *      Authorization Grants</a>
     *
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7523.html">RFC 7523
     *      JSON Web Token (JWT) Profile for OAuth 2.0 Client Authentication
     *      and Authorization Grants</a>
     */
    Response jwtBearer(TokenResponse tokenResponse);
}
