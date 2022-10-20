package de.netcetera.netceteratask.domain.repository;

import de.netcetera.netceteratask.domain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {



}
