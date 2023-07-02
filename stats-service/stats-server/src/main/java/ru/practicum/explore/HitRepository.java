package ru.practicum.explore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.explore.model.Stats(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit hit " +
            "WHERE hit.datetime BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.ip")
    List<Stats> findStatsByDistinctIp(LocalDateTime from, LocalDateTime to);

    @Query("SELECT new ru.practicum.explore.model.Stats(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit hit " +
            "WHERE hit.datetime BETWEEN ?1 AND ?2 " +
            "AND hit.uri IN ?3 " +
            "GROUP BY hit.ip")
    List<Stats> findStatsByUriDistinctIp(LocalDateTime from, LocalDateTime to, List<String> uris);                      // ТЕСТ 2.2

    @Query("SELECT new ru.practicum.explore.model.Stats(hit.app, hit.uri" + ", COUNT(hit.id)) " +
            "FROM Hit hit " +
            "WHERE hit.datetime BETWEEN ?1 AND ?2 " +
            "GROUP BY hit.uri")
    List<Stats> findStatsByDatetimeBetween(LocalDateTime from, LocalDateTime to);                                       // ТЕСТ 1.2

    @Query("SELECT new ru.practicum.explore.model.Stats(hit.app, hit.uri, COUNT(hit.id)) " +
            "FROM Hit hit " +
            "WHERE hit.datetime BETWEEN ?1 AND ?2 " +
            "AND hit.uri IN ?3 " +
            "GROUP BY hit.uri " +
            "ORDER BY COUNT(hit.id) DESC")
    List<Stats> findStatsByDatetimeBetweenAndUriIn(LocalDateTime from, LocalDateTime to, List<String> uris);            // ТЕСТ 1.1 / 2.1 / 3.1 / 3.2

}
