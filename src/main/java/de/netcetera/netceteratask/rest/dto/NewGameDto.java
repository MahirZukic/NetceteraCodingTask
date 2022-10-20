package de.netcetera.netceteratask.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewGameDto {

    private Long playerOne;

    private Long playerTwo;

}
