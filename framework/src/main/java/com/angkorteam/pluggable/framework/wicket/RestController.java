package com.angkorteam.pluggable.framework.wicket;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.IMultipartWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.upload.FileItem;
import org.apache.wicket.util.upload.FileUploadException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.angkorteam.pluggable.framework.FrameworkConstants;
import com.angkorteam.pluggable.framework.core.AbstractPlugin;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.WebSession;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.error.AccessDeniedPage;
import com.angkorteam.pluggable.framework.error.Error404Page;
import com.angkorteam.pluggable.framework.error.InternalErrorPage;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;
import com.angkorteam.pluggable.framework.utilities.RoleUtilities;
import com.angkorteam.pluggable.framework.utilities.SecurityUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.google.gson.Gson;

/**
 * @author Socheat KHAUV
 */
public class RestController implements IResource {

    public static final String PATH = "rest";

    private static final String AUTHORIZATION = "authorization";

    private static final String BASIC = "Basic";

    private static final String DIGEST = "Digest";

    private static final String AUTHENTICATION = "authentication";

    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * 
     */
    private static final long serialVersionUID = -8176249017311195277L;

    @Override
    public void respond(Attributes attributes) {
        AbstractWebApplication application = (AbstractWebApplication) AbstractWebApplication
                .get();

        String method = ((javax.servlet.http.HttpServletRequest) attributes
                .getRequest().getContainerRequest()).getMethod();
        WebRequest request = (WebRequest) attributes.getRequest();
        if (request.getHeader("Content-Type") != null
                && request.getHeader("Content-Type").startsWith(
                        "multipart/form-data")) {
            try {
                request = ((ServletWebRequest) request).newMultipartWebRequest(
                        Bytes.megabytes(10), "mp3");
            } catch (FileUploadException e) {
            }
        }
        WebResponse response = (WebResponse) attributes.getResponse();

        WebSession session = (WebSession) WebSession.get();
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        String path = attributes.getRequest().getUrl().getPath()
                .substring(RestController.PATH.length());
        RequestMappingInfo info = application.getControllers().get(path);

        if (info == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            RequestCycle.get().setResponsePage(Error404Page.class);
            return;
        }
        RequestMapping requestMapping = info.getMethod().getAnnotation(
                RequestMapping.class);
        if (requestMapping == null
                || requestMapping.method() != RequestMethod.valueOf(method
                        .toUpperCase())) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            RequestCycle.get().setResponsePage(Error404Page.class);
            return;
        }

        String secretKey = application.getSecretKey();

        String basicRealm = BASIC + " realm=\"" + application.getRealm() + "\"";
        long expiryTime = System.currentTimeMillis() + (300 * 1000);

        String signatureValue = DigestUtils
                .md5Hex(expiryTime + ":" + secretKey);

        String nonceValue = expiryTime + ":" + signatureValue;
        String nonceValueBase64 = new String(Base64.encodeBase64(nonceValue
                .getBytes()));

        // qop is quality of protection, as defined by RFC 2617.
        // we do not use opaque due to IE violation of RFC 2617 in not
        // representing opaque on subsequent requests in same session.
        String digestRealm = DIGEST + " realm=\"" + application.getRealm()
                + "\", " + "qop=\"auth\", nonce=\"" + nonceValueBase64 + "\"";

        // if (authException instanceof NonceExpiredException) {
        // authenticateHeader = authenticateHeader + ", stale=\"true\"";
        // }

        String authentication = DIGEST;
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization == null || "".equals(authorization)) {
            if (request.getHeader(AUTHENTICATION) != null
                    && !"".equals(request.getHeader(AUTHENTICATION))) {
                authentication = request.getHeader(AUTHENTICATION);
            }
        } else {
            if (authorization.startsWith("Basic")) {
                authentication = BASIC;
            } else if (authorization.startsWith("Digest")) {
                authentication = DIGEST;
            }
        }
        String username = null;

        Secured secured = info.getMethod().getAnnotation(Secured.class);
        if (secured != null) {
            if (secured.roles() != null && secured.roles().length > 0) {
                Roles roles = null;
                if (session.isSignedIn()) {
                    roles = session.getRoles();
                    username = session.getUsername();
                } else {
                    if (BASIC.equals(authentication)) {
                        if (authorization == null) {
                            response.setHeader(WWW_AUTHENTICATE, basicRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        String[] credential = new String(
                                Base64.decodeBase64(authorization.substring(6)
                                        .getBytes())).split(":");
                        if (credential == null || credential.length != 2) {
                            response.setHeader(WWW_AUTHENTICATE, basicRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        username = credential[0];
                        String password = credential[1];
                        boolean valid = SecurityUtilities.authenticateJdbc(
                                jdbcTemplate, username, password);
                        if (!valid) {
                            response.setHeader(WWW_AUTHENTICATE, basicRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        } else {
                            roles = new Roles();
                            for (String role : RoleUtilities.lookupJdbcRoles(
                                    jdbcTemplate, username)) {
                                roles.add(role);
                            }
                        }
                    } else if (DIGEST.equals(authentication)) {
                        if (authorization == null) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        DigestData digest = new DigestData(authorization);
                        if (!digest.validateAndDecode(secretKey,
                                application.getRealm())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        if (!SecurityUtilities.authenticate(jdbcTemplate,
                                digest.getUsername())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        String password = jdbcTemplate
                                .queryForObject(
                                        "select "
                                                + AbstractUser.PASSWORD
                                                + " from "
                                                + TableUtilities
                                                        .getTableName(AbstractUser.class)
                                                + " where "
                                                + AbstractUser.LOGIN + " = ?",
                                        String.class, digest.getUsername());

                        String serverDigestMd5 = digest.calculateServerDigest(
                                password, method);
                        if (!serverDigestMd5.equals(digest.getResponse())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        username = digest.getUsername();

                        boolean valid = SecurityUtilities.authenticateJdbc(
                                jdbcTemplate, username, password);
                        if (!valid) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        } else {
                            roles = new Roles();
                            for (String role : RoleUtilities.lookupJdbcRoles(
                                    jdbcTemplate, username)) {
                                roles.add(role);
                            }
                        }
                    }
                }
                Roles methodRole = new Roles();
                for (Role role : secured.roles()) {
                    methodRole.add(role.name());
                }
                if (!roles.hasAnyRole(methodRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    RequestCycle.get().setResponsePage(AccessDeniedPage.class);
                    return;
                }
            } else {
                boolean valid = false;
                valid = session.isSignedIn();
                if (!valid) {
                    if (BASIC.equals(authentication)) {
                        if (authorization == null) {
                            response.setHeader(WWW_AUTHENTICATE, basicRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        String[] credential = new String(
                                Base64.decodeBase64(authorization.substring(6)
                                        .getBytes())).split(":");
                        if (credential == null || credential.length != 2) {
                            response.setHeader(WWW_AUTHENTICATE, basicRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        username = credential[0];
                        String password = credential[1];
                        valid = SecurityUtilities.authenticateJdbc(
                                jdbcTemplate, username, password);
                    } else if (DIGEST.equals(authentication)) {
                        if (authorization == null) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        DigestData digest = new DigestData(authorization);
                        if (!digest.validateAndDecode(secretKey,
                                application.getRealm())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        if (!SecurityUtilities.authenticate(jdbcTemplate,
                                digest.getUsername())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        String password = jdbcTemplate
                                .queryForObject(
                                        "select "
                                                + AbstractUser.PASSWORD
                                                + " from "
                                                + TableUtilities
                                                        .getTableName(AbstractUser.class)
                                                + " where "
                                                + AbstractUser.LOGIN + " = ?",
                                        String.class, digest.getUsername());

                        String serverDigestMd5 = digest.calculateServerDigest(
                                password, method);
                        if (!serverDigestMd5.equals(digest.getResponse())) {
                            response.setHeader(WWW_AUTHENTICATE, digestRealm);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }

                        username = digest.getUsername();

                        valid = SecurityUtilities.authenticateJdbc(
                                jdbcTemplate, username, password);
                    }
                } else {
                    username = session.getUsername();
                }

                if (!valid) {
                    if (BASIC.equals(authentication)) {
                        response.setHeader(WWW_AUTHENTICATE, basicRealm);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    } else if (DIGEST.equals(authentication)) {
                        response.setHeader(WWW_AUTHENTICATE, digestRealm);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                } else {
                    if (!session.isSignedIn()) {
                        String password = jdbcTemplate
                                .queryForObject(
                                        "select "
                                                + AbstractUser.PASSWORD
                                                + " from "
                                                + TableUtilities
                                                        .getTableName(AbstractUser.class)
                                                + " where "
                                                + AbstractUser.LOGIN + " = ?",
                                        String.class, username);
                        session.signIn(username, password);
                    }
                }
            }
        }

        if (!application.isMigrated()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        }
        AbstractPlugin plugin = application.getPlugin(application
                .getPluginMapping(info.getClazz().getName()));
        if (plugin != null) {
            if (!plugin.isMigrated() || !plugin.isActivated()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                RequestCycle.get().setResponsePage(InternalErrorPage.class);
                return;
            }
        }

        if (info.getMethod().getAnnotation(Deprecated.class) != null) {
            Boolean allowdeprecated = application.select(
                    FrameworkConstants.DEPRECATED, Boolean.class);
            if (allowdeprecated == null) {
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                return;
            } else {
                if (!allowdeprecated) {
                    response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    return;
                }
            }
        }

        // UserPrincipal principal = null;
        // if (secured != null) {
        // principal = new UserPrincipal(username);
        // }

        try {
            Class<?>[] params = info.getMethod().getParameterTypes();
            Object result = null;
            if (params == null || params.length == 0) {
                result = (Object) MethodUtils.invokeMethod(info.getClazz()
                        .newInstance(), info.getMethod().getName());
            } else {
                Object[] args = new Object[info.getMethod().getParameterTypes().length];
                for (int i = 0; i < args.length; i++) {
                    Class<?> clazz = info.getMethod().getParameterTypes()[i];
                    if (clazz == javax.servlet.http.HttpServletRequest.class) {
                        args[i] = new HttpServletRequest(jdbcTemplate,
                                new UserPrincipal(username),
                                (javax.servlet.http.HttpServletRequest) request
                                        .getContainerRequest());
                    } else if (clazz == Principal.class) {
                        args[i] = new UserPrincipal(username);
                    } else if (clazz == HttpServletResponse.class) {
                        args[i] = (HttpServletResponse) response
                                .getContainerResponse();
                    } else if (clazz == WebRequest.class) {
                        args[i] = request;
                    } else if (clazz == WebResponse.class) {
                        args[i] = response;
                    } else if (clazz == AbstractWebApplication.class) {
                        args[i] = application;
                    } else if (clazz == JdbcTemplate.class) {
                        args[i] = application.getJdbcTemplate();
                    } else if (clazz == Gson.class) {
                        args[i] = application.getGson();
                    } else if (clazz == StringValue.class
                            || clazz == StringValue[].class) {
                        Annotation[] annons = info.getMethod()
                                .getParameterAnnotations()[i];
                        if (annons == null || annons.length == 0) {
                            throw new WicketRuntimeException(clazz.getName()
                                    + " is ambiguous");
                        } else {
                            ApiParam param = null;
                            for (Annotation annon : annons) {
                                if (annon.annotationType() == ApiParam.class) {
                                    param = (ApiParam) annon;
                                    break;
                                }
                            }
                            if (param == null || param.name() == null
                                    || "".equals(param.name())) {
                                throw new WicketRuntimeException(
                                        clazz.getName() + " is ambiguous");
                            }
                            if (param.type().isArray()) {
                                args[i] = request.getRequestParameters()
                                        .getParameterValues(param.name());
                            } else {
                                args[i] = request.getRequestParameters()
                                        .getParameterValue(param.name());
                            }
                        }
                    } else if (clazz == FileItem.class
                            || clazz == FileItem[].class) {
                        Annotation[] annons = info.getMethod()
                                .getParameterAnnotations()[i];
                        if (annons == null || annons.length == 0) {
                            throw new WicketRuntimeException(clazz.getName()
                                    + " is ambiguous");
                        } else {
                            ApiParam param = null;
                            for (Annotation annon : annons) {
                                if (annon.annotationType() == ApiParam.class) {
                                    param = (ApiParam) annon;
                                    break;
                                }
                            }
                            if (param == null || param.name() == null
                                    || "".equals(param.name())) {
                                throw new WicketRuntimeException(
                                        clazz.getName() + " is ambiguous");
                            }
                            if (request instanceof IMultipartWebRequest) {
                                IMultipartWebRequest partRequest = (IMultipartWebRequest) request;
                                List<FileItem> fileItems = partRequest
                                        .getFile(param.name());
                                if (fileItems != null && !fileItems.isEmpty()) {
                                    if (param.type().isArray()) {
                                        args[i] = fileItems
                                                .toArray(new FileItem[fileItems
                                                        .size()]);
                                    } else {
                                        args[i] = fileItems.get(0);
                                    }
                                }
                            }
                        }
                    } else {
                        throw new WicketRuntimeException(clazz.getName()
                                + " is not supported");
                    }
                }
                result = (Object) MethodUtils.invokeMethod(info.getClazz()
                        .newInstance(), info.getMethod().getName(), args);
            }

            if (result == null) {
                throw new NullPointerException(info.getMethod().getName()
                        + " must return a Result object");
            } else {
                if (!(result instanceof Result)) {
                    throw new ClassCastException(info.getMethod().getName()
                            + " must return type is not a Result object");
                } else {
                    Result<?> object = (Result<?>) result;
                    response.setContentType(object.getContentType());
                    response.setStatus(object.getStatus());
                }
            }
            return;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        } catch (InstantiationException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        } catch (java.lang.NoSuchMethodError e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            RequestCycle.get().setResponsePage(InternalErrorPage.class);
            return;
        }

    }

    private class UserPrincipal implements Principal {

        private String name;

        public UserPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private class DigestData {
        private final String username;
        private final String realm;
        private final String nonce;
        private final String uri;
        private final String response;
        private final String qop;
        private final String nc;
        private final String cnonce;
        private final String section212response;
        private long nonceExpiryTime;

        public DigestData(String header) {
            section212response = header.substring(7);
            String[] headerEntries = DigestAuthUtils.splitIgnoringQuotes(
                    section212response, ',');
            Map<String, String> headerMap = DigestAuthUtils
                    .splitEachArrayElementAndCreateMap(headerEntries, "=", "\"");

            username = headerMap.get("username");
            realm = headerMap.get("realm");
            nonce = headerMap.get("nonce");
            uri = headerMap.get("uri");
            response = headerMap.get("response");
            qop = headerMap.get("qop"); // RFC 2617 extension
            nc = headerMap.get("nc"); // RFC 2617 extension
            cnonce = headerMap.get("cnonce"); // RFC 2617 extension

        }

        public boolean validateAndDecode(String entryPointKey,
                String expectedRealm) {
            // Check all required parameters were supplied (ie RFC 2069)
            if ((username == null) || (realm == null) || (nonce == null)
                    || (uri == null) || (response == null)) {
                return false;
            }
            // Check all required parameters for an "auth" qop were supplied (ie
            // RFC 2617)
            if ("auth".equals(qop)) {
                if ((nc == null) || (cnonce == null)) {
                    return false;
                }
            }

            // Check realm name equals what we expected
            if (!expectedRealm.equals(realm)) {
                return false;
            }

            // Check nonce was Base64 encoded (as sent by
            // DigestAuthenticationEntryPoint)
            try {
                Base64.decodeBase64(nonce.getBytes());
            } catch (Throwable e) {
                return false;
            }

            // Decode nonce from Base64
            // format of nonce is:
            // base64(expirationTime + ":" + md5Hex(expirationTime + ":" + key))
            String nonceAsPlainText = new String(Base64.decodeBase64(nonce
                    .getBytes()));
            String[] nonceTokens = StringUtils.delimitedListToStringArray(
                    nonceAsPlainText, ":");

            if (nonceTokens.length != 2) {
                return false;
            }

            // Extract expiry time from nonce

            try {
                nonceExpiryTime = new Long(nonceTokens[0]).longValue();
            } catch (NumberFormatException nfe) {
                return false;
            }

            // Check signature of nonce matches this expiry time
            String expectedNonceSignature = DigestAuthUtils
                    .md5Hex(nonceExpiryTime + ":" + entryPointKey);

            if (!expectedNonceSignature.equals(nonceTokens[1])) {
                return false;
            }
            return true;
        }

        public String calculateServerDigest(String password, String httpMethod) {
            // Compute the expected response-digest (will be in hex form)

            // Don't catch IllegalArgumentException (already checked validity)
            return DigestAuthUtils.generateDigest(false, username, realm,
                    password, httpMethod, uri, qop, nonce, nc, cnonce);
        }

        String getUsername() {
            return username;
        }

        String getResponse() {
            return response;
        }
    }

    private static class DigestAuthUtils {

        private static final String[] EMPTY_STRING_ARRAY = new String[0];

        static String encodePasswordInA1Format(String username, String realm,
                String password) {
            String a1 = username + ":" + realm + ":" + password;

            return md5Hex(a1);
        }

        static String[] splitIgnoringQuotes(String str, char separatorChar) {
            if (str == null) {
                return null;
            }

            int len = str.length();

            if (len == 0) {
                return EMPTY_STRING_ARRAY;
            }

            List<String> list = new ArrayList<String>();
            int i = 0;
            int start = 0;
            boolean match = false;

            while (i < len) {
                if (str.charAt(i) == '"') {
                    i++;
                    while (i < len) {
                        if (str.charAt(i) == '"') {
                            i++;
                            break;
                        }
                        i++;
                    }
                    match = true;
                    continue;
                }
                if (str.charAt(i) == separatorChar) {
                    if (match) {
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
            if (match) {
                list.add(str.substring(start, i));
            }

            return list.toArray(new String[list.size()]);
        }

        /**
         * Computes the <code>response</code> portion of a Digest authentication
         * header. Both the server and user agent should compute the
         * <code>response</code> independently. Provided as a static method to
         * simplify the coding of user agents.
         * 
         * @param passwordAlreadyEncoded
         *            true if the password argument is already encoded in the
         *            correct format. False if it is plain text.
         * @param username
         *            the user's login name.
         * @param realm
         *            the name of the realm.
         * @param password
         *            the user's password in plaintext or ready-encoded.
         * @param httpMethod
         *            the HTTP request method (GET, POST etc.)
         * @param uri
         *            the request URI.
         * @param qop
         *            the qop directive, or null if not set.
         * @param nonce
         *            the nonce supplied by the server
         * @param nc
         *            the "nonce-count" as defined in RFC 2617.
         * @param cnonce
         *            opaque string supplied by the client when qop is set.
         * @return the MD5 of the digest authentication response, encoded in hex
         * @throws IllegalArgumentException
         *             if the supplied qop value is unsupported.
         */
        static String generateDigest(boolean passwordAlreadyEncoded,
                String username, String realm, String password,
                String httpMethod, String uri, String qop, String nonce,
                String nc, String cnonce) throws IllegalArgumentException {
            String a1Md5;
            String a2 = httpMethod + ":" + uri;
            String a2Md5 = md5Hex(a2);

            if (passwordAlreadyEncoded) {
                a1Md5 = password;
            } else {
                a1Md5 = DigestAuthUtils.encodePasswordInA1Format(username,
                        realm, password);
            }

            String digest;

            if (qop == null) {
                // as per RFC 2069 compliant clients (also reaffirmed by RFC
                // 2617)
                digest = a1Md5 + ":" + nonce + ":" + a2Md5;
            } else if ("auth".equals(qop)) {
                // As per RFC 2617 compliant clients
                digest = a1Md5 + ":" + nonce + ":" + nc + ":" + cnonce + ":"
                        + qop + ":" + a2Md5;
            } else {
                throw new IllegalArgumentException(
                        "This method does not support a qop: '" + qop + "'");
            }

            return md5Hex(digest);
        }

        /**
         * Takes an array of <code>String</code>s, and for each element removes
         * any instances of <code>removeCharacter</code>, and splits the element
         * based on the <code>delimiter</code>. A <code>Map</code> is then
         * generated, with the left of the delimiter providing the key, and the
         * right of the delimiter providing the value.
         * <p>
         * Will trim both the key and value before adding to the
         * <code>Map</code>.
         * </p>
         * 
         * @param array
         *            the array to process
         * @param delimiter
         *            to split each element using (typically the equals symbol)
         * @param removeCharacters
         *            one or more characters to remove from each element prior
         *            to attempting the split operation (typically the quotation
         *            mark symbol) or <code>null</code> if no removal should
         *            occur
         * @return a <code>Map</code> representing the array contents, or
         *         <code>null</code> if the array to process was null or empty
         */
        static Map<String, String> splitEachArrayElementAndCreateMap(
                String[] array, String delimiter, String removeCharacters) {
            if ((array == null) || (array.length == 0)) {
                return null;
            }

            Map<String, String> map = new HashMap<String, String>();

            for (String s : array) {
                String postRemove;

                if (removeCharacters == null) {
                    postRemove = s;
                } else {
                    postRemove = StringUtils.replace(s, removeCharacters, "");
                }

                String[] splitThisArrayElement = split(postRemove, delimiter);

                if (splitThisArrayElement == null) {
                    continue;
                }

                map.put(splitThisArrayElement[0].trim(),
                        splitThisArrayElement[1].trim());
            }

            return map;
        }

        /**
         * Splits a <code>String</code> at the first instance of the delimiter.
         * <p>
         * Does not include the delimiter in the response.
         * </p>
         * 
         * @param toSplit
         *            the string to split
         * @param delimiter
         *            to split the string up with
         * @return a two element array with index 0 being before the delimiter,
         *         and index 1 being after the delimiter (neither element
         *         includes the delimiter)
         * @throws IllegalArgumentException
         *             if an argument was invalid
         */
        static String[] split(String toSplit, String delimiter) {
            Assert.hasLength(toSplit, "Cannot split a null or empty string");
            Assert.hasLength(delimiter,
                    "Cannot use a null or empty delimiter to split a string");

            if (delimiter.length() != 1) {
                throw new IllegalArgumentException(
                        "Delimiter can only be one character in length");
            }

            int offset = toSplit.indexOf(delimiter);

            if (offset < 0) {
                return null;
            }

            String beforeDelimiter = toSplit.substring(0, offset);
            String afterDelimiter = toSplit.substring(offset + 1);

            return new String[] { beforeDelimiter, afterDelimiter };
        }

        static String md5Hex(String data) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("No MD5 algorithm available!");
            }

            return new String(Hex.encode(digest.digest(data.getBytes())));
        }
    }

    private class HttpServletRequest extends HttpServletRequestWrapper {

        private UserPrincipal principal;

        private List<String> roles;

        private JdbcTemplate jdbcTemplate;

        public HttpServletRequest(JdbcTemplate jdbcTemplate,
                UserPrincipal principal,
                javax.servlet.http.HttpServletRequest request) {
            super(request);
            this.principal = principal;
            this.jdbcTemplate = jdbcTemplate;
            if (principal != null) {
                roles = RoleUtilities.lookupJdbcRoles(jdbcTemplate,
                        principal.getName());
            } else {
                roles = Collections.emptyList();
            }
        }

        @Override
        public Principal getUserPrincipal() {
            return this.principal;
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

    }

    private static class Hex {

        private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        public static char[] encode(byte[] bytes) {
            final int nBytes = bytes.length;
            char[] result = new char[2 * nBytes];

            int j = 0;
            for (int i = 0; i < nBytes; i++) {
                // Char for top 4 bits
                result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
                // Bottom 4
                result[j++] = HEX[(0x0F & bytes[i])];
            }

            return result;
        }
    }

}
