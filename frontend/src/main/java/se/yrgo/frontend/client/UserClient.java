
package se.yrgo.frontend.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.yrgo.frontend.security.RemoteAuthenticationProvider;

import java.util.List;
import java.util.Map;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://user-service")
                .build();
    }

    public List<Map<String, Object>> getAllUsers() {
        return restClient.get()
                .uri("/api/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Map<String, Object> getUser(Long userId) {
        return restClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void createUser(Map<String, String> user) {
        restClient.post()
                .uri("/api/users")
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    public void updateUser(Long userId, Map<String, String> user) {
        restClient.put()
                .uri("/api/users/{id}", userId)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteUser(Long userId) {
        restClient.delete()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .toBodilessEntity();
    }

    public List<Map<String, Object>> getAddresses(Long userId) {
        return restClient.get()
                .uri("/api/users/{id}/addresses", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void addAddress(Long userId, Map<String, String> address) {
        restClient.post()
                .uri("/api/users/{id}/addresses", userId)
                .body(address)
                .retrieve()
                .toBodilessEntity();
    }

    public void updateAddress(Long addressId, Map<String, String> address) {
        restClient.put()
                .uri("/api/addresses/{id}", addressId)
                .body(address)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteAddress(Long addressId) {
        restClient.delete()
                .uri("/api/addresses/{id}", addressId)
                .retrieve()
                .toBodilessEntity();
    }

    public RemoteAuthenticationProvider.UserResponse authenticate(
            String email, String password) {

        return restClient.post()
                .uri("/api/users/authenticate")
                .body(new RemoteAuthenticationProvider.LoginRequest(
                        email, password
                ))
                .retrieve()
                .body(RemoteAuthenticationProvider.UserResponse.class);
    }
}
