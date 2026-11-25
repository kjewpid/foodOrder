
package se.yrgo.frontend.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import se.yrgo.frontend.dto.RegisterForm;
import se.yrgo.frontend.exception.UserServiceUnavailableException;
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

    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    public List<Map<String, Object>> getAllUsers() {
        return restClient.get()
                .uri("/api/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public Map<String, Object> getUser(Long userId) {
        return restClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "createUserFallback")
    public void createUser(RegisterForm form) {
        restClient.post()
                .uri("/api/users")
                .body(form)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "updateUserFallback")
    public void updateUser(Long userId, Map<String, String> user) {
        restClient.put()
                .uri("/api/users/{id}", userId)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "deleteUserFallback")
    public void deleteUser(Long userId) {
        restClient.delete()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getAddressesFallback")
    public List<Map<String, Object>> getAddresses(Long userId) {
        return restClient.get()
                .uri("/api/users/{id}/addresses", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "addAddressFallback")
    public void addAddress(Long userId, Map<String, String> address) {
        restClient.post()
                .uri("/api/users/{id}/addresses", userId)
                .body(address)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "updateAddressFallback")
    public void updateAddress(Long addressId, Map<String, String> address) {
        restClient.put()
                .uri("/api/addresses/{id}", addressId)
                .body(address)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "deleteAddressFallback")
    public void deleteAddress(Long addressId) {
        restClient.delete()
                .uri("/api/addresses/{id}", addressId)
                .retrieve()
                .toBodilessEntity();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "authenticateFallback")
    public RemoteAuthenticationProvider.UserResponse authenticate(
            String email, String password) {

        return restClient.post()
                .uri("/api/users/authenticate")
                .body(new RemoteAuthenticationProvider.LoginRequest(
                        email, password))
                .retrieve()
                .body(RemoteAuthenticationProvider.UserResponse.class);
    }

    // Fallback methods

    public List<Map<String, Object>> getAllUsersFallback(
            Throwable throwable) {
        throw handleFailure(throwable);
    }

    public Map<String, Object> getUserFallback(
            Long userId, Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void createUserFallback(
            RegisterForm form, Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void updateUserFallback(
            Long userId, Map<String, String> user,
            Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void deleteUserFallback(
            Long userId, Throwable throwable) {
        throw handleFailure(throwable);
    }

    public List<Map<String, Object>> getAddressesFallback(
            Long userId, Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void addAddressFallback(
            Long userId, Map<String, String> address,
            Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void updateAddressFallback(
            Long addressId, Map<String, String> address,
            Throwable throwable) {
        throw handleFailure(throwable);
    }

    public void deleteAddressFallback(
            Long addressId, Throwable throwable) {
        throw handleFailure(throwable);
    }

    public RemoteAuthenticationProvider.UserResponse authenticateFallback(
            String email, String password, Throwable throwable) {
        throw handleFailure(throwable);
    }

    private RuntimeException handleFailure(Throwable throwable) {

        if (throwable instanceof HttpClientErrorException exception) {
            return exception;
        }

        System.out.println("UserService fallback: "
                + throwable.getClass().getSimpleName());

        return new UserServiceUnavailableException(throwable);
    }
}
