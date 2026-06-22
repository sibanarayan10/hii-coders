package com.sibanarayan.code.websocket;


import com.sibanarayan.code.models.request.ProblemPresenceRequest;
import com.sibanarayan.code.models.response.PresenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceTracker presenceTracker;
    private final WebSocketEventListener eventListener;


    @MessageMapping("/presence/enter")
    public void enterProblem(
            @Payload ProblemPresenceRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        UUID userId = getUserId(headerAccessor);
        if (userId == null || request.getProblemId() == null) return;

        presenceTracker.userEnteredProblem(userId, request.getProblemId());
        eventListener.broadcastProblemPresence(request.getProblemId());
        eventListener.broadcastPlatformPresence();
    }


    @MessageMapping("/presence/leave")
    public void leaveProblem(
            @Payload ProblemPresenceRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        UUID userId = getUserId(headerAccessor);
        if (userId == null || request.getProblemId() == null) return;

        presenceTracker.userLeftProblem(userId, request.getProblemId());

        eventListener.broadcastProblemPresence(request.getProblemId());
        eventListener.broadcastPlatformPresence();
    }


    @GetMapping("/api/v1/presence/platform")
    @ResponseBody
    public PresenceResponse getPlatformPresence() {
        return new PresenceResponse(
                presenceTracker.getOnlineCount(),
                presenceTracker.getAllProblemViewerCounts()
        );
    }


    @GetMapping("/api/v1/presence/problem/{problemId}")
    @ResponseBody
    public Map<String, Object> getProblemPresence(@PathVariable String problemId) {
        return Map.of(
                "problemId", problemId,
                "viewerCount", presenceTracker.getProblemViewerCount(problemId)
        );
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private UUID getUserId(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> attrs = headerAccessor.getSessionAttributes();
        if (attrs == null) return null;
        return (UUID) attrs.get("userId");
    }
}
