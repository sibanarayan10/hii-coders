package com.sibanarayan.code.websocket;

import com.sibanarayan.code.models.response.PresenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PresenceTracker presenceTracker;
    private final SimpMessagingTemplate messagingTemplate;


    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<UUID, ScheduledFuture<?>> pendingDisconnects = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs == null) return;

        String sessionId = accessor.getSessionId();
        UUID userId = (UUID) attrs.get("userId");
        if (sessionId == null || userId == null) return;


        ScheduledFuture<?> pending = pendingDisconnects.remove(userId);
        if (pending != null) {
            pending.cancel(false);
            log.debug("WS reconnect detected for userId={}, cancelled pending disconnect broadcast", userId);
        }

        presenceTracker.userConnected(sessionId, userId);
        log.debug("WS connected: userId={}, session={}", userId, sessionId);

        broadcastPlatformPresence();
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId == null) return;

        UUID userId = presenceTracker.userDisconnected(sessionId);
        log.debug("WS disconnected: userId={}, session={}", userId, sessionId);

        if (userId == null) return;

        final UUID finalUserId = userId;
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            pendingDisconnects.remove(finalUserId);
            broadcastPlatformPresence();
            log.debug("Broadcast after confirmed disconnect: userId={}", finalUserId);
        }, 300, TimeUnit.MILLISECONDS);

        pendingDisconnects.put(userId, future);
    }

    public void broadcastPlatformPresence() {
        PresenceResponse payload = new PresenceResponse(
                presenceTracker.getOnlineCount(),
                presenceTracker.getAllProblemViewerCounts()
        );
        messagingTemplate.convertAndSend("/topic/presence/platform", payload);
    }

    public void broadcastProblemPresence(String problemId) {
        int count = presenceTracker.getProblemViewerCount(problemId);
        messagingTemplate.convertAndSend(
                "/topic/presence/problem/" + problemId,
                Map.of("problemId", problemId, "viewerCount", count)
        );
    }
}
