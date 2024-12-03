package com.vssk.demo.golf.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerGameScore {
    private String name;
    private Integer hole;
    private Integer score;
    private String scorePar;
}
