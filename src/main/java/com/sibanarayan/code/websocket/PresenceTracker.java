package com.sibanarayan.code.websocket;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class PresenceTracker {

    private final Map<String, UUID> sessionToUser = new ConcurrentHashMap<>();
    private final Map<UUID, String> userToSession = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> problemViewers = new ConcurrentHashMap<>();


    public void userConnected(String sessionId, UUID userId) {
        sessionToUser.put(sessionId, userId);
        userToSession.put(userId, sessionId);
    }

    public UUID userDisconnected(String sessionId) {
        UUID userId = sessionToUser.remove(sessionId);
        if (userId != null) {
            userToSession.remove(userId);
            problemViewers.values().forEach(viewers -> viewers.remove(userId));
            problemViewers.entrySet().removeIf(e -> e.getValue().isEmpty());
        }
        return userId;
    }

    public void userEnteredProblem(UUID userId, String problemId) {
        problemViewers
                .computeIfAbsent(problemId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void userLeftProblem(UUID userId, String problemId) {
        Set<UUID> viewers = problemViewers.get(problemId);
        if (viewers != null) {
            viewers.remove(userId);
            if (viewers.isEmpty()) {
                problemViewers.remove(problemId);
            }
        }
    }


    public int getOnlineCount() {
        return userToSession.size();
    }

    public int getProblemViewerCount(String problemId) {
        Set<UUID> viewers = problemViewers.get(problemId);
        return viewers == null ? 0 : viewers.size();
    }

    public Map<String, Integer> getAllProblemViewerCounts() {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        problemViewers.forEach((problemId, viewers) ->
                result.put(problemId, viewers.size()));
        return Collections.unmodifiableMap(result);
    }

    public boolean isOnline(UUID userId) {
        return userToSession.containsKey(userId);
    }
}
