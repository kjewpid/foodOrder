package se.yrgo.frontend.security;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.security.Principal;
import java.util.List;

@Component
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    private final RestClient restClient;

    public RemoteAuthenticationProvider(
            @LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            UserResponse user = restClient.post()
                    .uri("http://user-service/api/users/authenticate")
                    .body(new LoginRequest(email, password))
                    .retrieve()
                    .body(UserResponse.class);

            if (user == null) {
                throw new BadCredentialsException("Felaktiga inloggningsuppgifter");
            }

            System.out.println("Inloggad användare, ID: " + user.id());

            return new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
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
            String email
    ) implements Principal {
        @Override
        public String getName() {
            return email;
        }
    }
}
