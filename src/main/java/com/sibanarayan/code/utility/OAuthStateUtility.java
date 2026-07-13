package com.sibanarayan.code.utility;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Encodes/decodes the OAuth2 "state" parameter so it can carry the
 * frontend "source" path (e.g. /problems/{id}) through the Google/Github
 * redirect round trip, in addition to its usual CSRF-protection role.
 *
 * Format (before base64url encoding): "<nonce>|<source>"
 * The nonce keeps the state unguessable even when source is empty,
 * preserving CSRF protection.
 */
public final class OAuthStateUtility {

    private static final String DELIMITER = "|";

    private OAuthStateUtility() {
    }

    public static String encode(String source) {
        String nonce = UUID.randomUUID().toString();
        String payload = (source == null || source.isBlank())
                ? nonce
                : nonce + DELIMITER + source;

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @return the decoded source path, or null if absent/unparseable.
     */
    public static String decodeSource(String state) {
        if (state == null || state.isBlank()) {
            return null;
        }

        try {
            String payload = new String(
                    Base64.getUrlDecoder().decode(state),
                    StandardCharsets.UTF_8
            );

            int delimiterIndex = payload.indexOf(DELIMITER);
            if (delimiterIndex < 0 || delimiterIndex == payload.length() - 1) {
                return null;
            }

            return payload.substring(delimiterIndex + 1);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Only allow same-site relative paths as redirect targets.
     * Blocks absolute/protocol-relative URLs (open-redirect protection).
     */
    public static String sanitizeSource(String source) {
        if (source == null
                || !source.startsWith("/")
                || source.startsWith("//")
                || source.contains("://")) {
            return "";
        }
        return source;
    }
}
