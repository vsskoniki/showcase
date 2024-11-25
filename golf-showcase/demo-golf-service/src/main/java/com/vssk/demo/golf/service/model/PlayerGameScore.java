package com.vssk.demo.golf.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlayerGameScore {
    private String name;
    private Integer hole;
    private Integer score;
    private String scorePar;
}
