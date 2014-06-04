package org.turbogwt.net.http.client.jackson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the media types that the methods of a resource class or
 * {@link javax.ws.rs.ext.MessageBodyReader} can accept. If
 * not specified, a container will assume that any media type is acceptable.
 * Method level annotations override a class level annotation. A container
 * is responsible for ensuring that the method invoked is capable of consuming
 * the media type of the HTTP request entity body. If no such method is
 * available the container must respond with a HTTP "415 Unsupported Media Type"
 * as specified by RFC 2616.
 *
 * @see javax.ws.rs.ext.MessageBodyReader
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GwtJacksonDeserialize {
    /**
     * A list of media types. Each entry may specify a single type or consist
     * of a comma separated list of types. E.g. {"image/jpeg,image/gif",
     * "image/png"}. Use of the comma-separated form allows definition of a
     * common string constant for use on multiple targets.
     */
    String[] value() default "*/*";
}
