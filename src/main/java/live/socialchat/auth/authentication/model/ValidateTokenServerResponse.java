package live.socialchat.auth.authentication.model;

import live.socialchat.auth.exception.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ValidateTokenServerResponse {
    
    private final String userId;
    private final String sessionId;
    private final String message;
    private final ResponseStatus status;
    
}
