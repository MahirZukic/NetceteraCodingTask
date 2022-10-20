package de.netcetera.netceteratask.domain.repository;

import de.netcetera.netceteratask.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {



}
