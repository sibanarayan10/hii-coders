package com.sibanarayan.code.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EngagementRequest {
    private Boolean liked;
    private Boolean saved;
    private Boolean favorite;
}
