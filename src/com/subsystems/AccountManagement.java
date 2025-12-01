package com.subsystems;

import com.broker.AsyncMessageBroker;
import com.broker.EventType;
import com.broker.Listener;
import com.broker.Message;
import com.common.dto.auth.LoginRequest;
import com.common.dto.auth.RegistrationRequest;
import com.common.dto.account.AccountViewRequest;
import com.services.SessionManager;
import java.util.HashMap;
import java.util.Map;
import com.entities.User;

import java.util.concurrent.CompletableFuture;
import com.managers.account.RegisterManager;
import com.managers.account.LoginManager;
import com.managers.account.ViewAccountManager;

// Handle USER_REGISTER_REQUESTED, USER_LOGIN_REQUEST and other account events
// !Handle register: 
//Cast payload to a DTO through a registration form. Validate, hash password, insert user via Database.java. On success publish USER_REGISTER_SUCCESS with created User or public USER_REGISTER_FAILED with reason

// !Handle login: 
//Check credentials via AuthenticationService, publish USER_LOGIN_SUCCESS/FAILED and set session info

public class AccountManagement implements Subsystems {
    private final RegisterManager registerManager;
    private final LoginManager loginManager;
    private final ViewAccountManager viewAccountManager;
    private AsyncMessageBroker broker;

    public AccountManagement(RegisterManager rm, LoginManager lm, ViewAccountManager vam) {
        this.registerManager = rm;
        this.loginManager = lm;
        this.viewAccountManager = vam;
    }

    public void publishEvent(EventType eventType, Object payload) {
        broker.publish(eventType, payload);
    }

    @Override
    public void init(AsyncMessageBroker broker) {
        this.broker = broker;
        broker.registerListener(EventType.USER_REGISTER_REQUESTED, this::handleRegister);
        broker.registerListener(EventType.USER_LOGIN_REQUEST, this::handleLogin);
        broker.registerListener(EventType.ACCOUNT_VIEW_REQUESTED, this::handleAccountView);
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        broker.unregisterListener(EventType.USER_REGISTER_REQUESTED, this::handleRegister);
        broker.unregisterListener(EventType.USER_LOGIN_REQUEST, this::handleLogin);
        broker.unregisterListener(EventType.ACCOUNT_VIEW_REQUESTED, this::handleAccountView);
    }

    private CompletableFuture<Void> handleRegister(Message message) {
        return CompletableFuture.runAsync(() -> {
            if (!(message.getPayload() instanceof RegistrationRequest request))
                return;

            try {
                User newUser = registerManager.register(request);
                String token = SessionManager.getInstance().createSession(newUser);
                newUser.setPassword(null);

                Map<String, Object> payload = new HashMap<>();
                payload.put("user", newUser);
                payload.put("token", token);
                broker.publish(EventType.USER_REGISTER_SUCCESS, payload);
                System.out.println("[AccountManagement] Register Success");

            } catch (Exception ex) {
                broker.publish(EventType.USER_REGISTER_FAILED, "Database Error");
                System.out.println("[AccountManagement] Registration DB error: " + ex.getMessage());
            }
        });
    }

    private CompletableFuture<Void> handleLogin(Message message) {
        return CompletableFuture.runAsync(() -> {
            if (!(message.getPayload() instanceof LoginRequest request))
                return;

            try {
                User user = loginManager.login(request.getUsername(), request.getPassword());

                String token = SessionManager.getInstance().createSession(user);
                user.setPassword(null);

                Map<String, Object> payload = new HashMap<>();
                payload.put("user", user);
                payload.put("token", token);
                broker.publish(EventType.USER_LOGIN_SUCCESS, payload);
                System.out.println("[AccountManagement] Login Success");
            } catch (Exception ex) {
                broker.publish(EventType.USER_LOGIN_FAILED, "Database Error");
                System.out.println("[AccountManagement] Login DB error: " + ex.getMessage());
            }
        });
    }

    private CompletableFuture<Void> handleAccountView(Message message) {
        return CompletableFuture.runAsync(() -> {
            if (!(message.getPayload() instanceof AccountViewRequest request))
                return;

            try {
                User user = viewAccountManager.viewAccount(request.getUserId());
                if (user != null) {
                    user.setPassword(null); // Don't send password
                    broker.publish(EventType.ACCOUNT_VIEW_RETURNED, user);
                    System.out.println("[AccountManagement] Account view returned for user: " + user.getUsername());
                } else {
                    broker.publish(EventType.ACCOUNT_VIEW_RETURNED, null);
                    System.out.println("[AccountManagement] User not found for account view");
                }
            } catch (Exception ex) {
                broker.publish(EventType.ACCOUNT_VIEW_RETURNED, null);
                System.out.println("[AccountManagement] Account view error: " + ex.getMessage());
            }
        });
    }
}