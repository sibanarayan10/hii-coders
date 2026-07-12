package com.sibanarayan.code.models.embeddings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public  class Block {

    private String id;

    private String type;

    private Map<String, Object> data;

}
