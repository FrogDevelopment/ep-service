package fr.frogdevelopment.ep.configuration.security;

import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import fr.frogdevelopment.ep.views.login.LoginView;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils takes care of all such static operations that have to do with security and querying rights from
 * different beans of the UI.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SecurityUtils {

    /**
     * Tests if the request is an internal framework request. The test consists of checking if the request parameter is
     * present and if its value is consistent with any of the request types know.
     *
     * @param request {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final var parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    static boolean isAccessGranted(Class<?> securedClass) {
        if (LoginView.class.equals(securedClass)) {
            return true;
        }

        if (!isUserLoggedIn()) {
            return false;
        }

        // Allow if no roles are required.
        var secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
        if (secured == null) {
            return true;
        }

        // lookup needed role in user roles
        var allowedRoles = Arrays.asList(secured.value());
        var userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return userAuthentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);
    }

    static boolean isUserLoggedIn() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }
}
