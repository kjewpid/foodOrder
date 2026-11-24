
package se.yrgo.frontend.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import se.yrgo.frontend.client.UserClient;

import java.security.Principal;
import java.util.List;

@Component
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    private final UserClient userClient;

    public RemoteAuthenticationProvider(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UserResponse user = userClient.authenticate(email, password);

            if (user == null) {
                throw new BadCredentialsException(
                        "Felaktiga inloggningsuppgifter"
                );
            }

            System.out.println("Inloggad användare, ID: " + user.id());
            System.out.println("Roll: " + user.role());

            return new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority(
                            "ROLE_" + user.role()
                    ))
            );

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException(
                    "Felaktig e-post eller lösenord", e
            );
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }

    public record LoginRequest(String email, String password) {
    }

    public record UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String role
    ) implements Principal {

        @Override
        public String getName() {
            return email;
        }
    }
}
