package com.vssk.demo.golf.service.repository;

import com.vssk.demo.golf.service.entity.PlayerScoresEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<PlayerScoresEntity, String> {

}
