package de.netcetera.netceteratask.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameMoveDto {

    private Long gameId;

    private Integer move;

}
