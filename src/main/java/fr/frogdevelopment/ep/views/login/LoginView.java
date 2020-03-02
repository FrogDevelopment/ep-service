package fr.frogdevelopment.ep.views.login;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.frogdevelopment.ep.configuration.security.CustomRequestCache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public static final String ROUTE = "login";

    private final LoginOverlay login = new LoginOverlay();

    public LoginView(AuthenticationManager authenticationManager,
                     CustomRequestCache requestCache) {
        login.setOpened(true);
        login.setTitle("Solidays - EP");
        login.setDescription("Gestion des bénévoles et du planning des Entrées Publiques");

        add(login);

        login.addLoginListener(e -> {
            try {
                // try to authenticate with given credentials, should always return not null or throw an {@link AuthenticationException}
                var authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(e.getUsername(), e.getPassword()));

                // if authentication was successful we will update the security context and redirect to the page requested first
                SecurityContextHolder.getContext().setAuthentication(authentication);
                login.close();
                UI.getCurrent().navigate(requestCache.resolveRedirectUrl());
            } catch (AuthenticationException ex) {
                login.setError(true);
            }
        });
    }
}
