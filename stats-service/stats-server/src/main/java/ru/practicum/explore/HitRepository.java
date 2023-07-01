package ru.practicum.explore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Hit;

import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {

    /*@Query("SELECT hit FROM Hit hit " +
            "JOIN stat.hit stat" +
            "WHERE stat.app = ?1 AND stat.uri = ?2")*/
    List<>

}
