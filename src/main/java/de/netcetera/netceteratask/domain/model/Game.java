package de.netcetera.netceteratask.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import de.netcetera.netceteratask.domain.enums.GameResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Game {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "player_one", nullable = false)
    private Long playerOne;

    @Column(name = "player_two", nullable = false)
    private Long playerTwo;

    @Column(name = "moves", nullable = true)
    private Integer moves;

    @Column(name = "game_result", nullable = true)
    private GameResult result;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

}
