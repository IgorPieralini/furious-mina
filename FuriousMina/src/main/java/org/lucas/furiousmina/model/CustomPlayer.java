package org.lucas.furiousmina.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomPlayer {
    private String id;
    private String name;
    private int blocosQuebrados;
}
