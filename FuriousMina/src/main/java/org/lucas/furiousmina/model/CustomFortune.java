package org.lucas.furiousmina.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomFortune {

    int fortuneLevel;
    int nextRequiredBlocks;
}
