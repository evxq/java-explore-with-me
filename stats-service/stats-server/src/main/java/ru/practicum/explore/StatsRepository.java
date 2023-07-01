package ru.practicum.explore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Integer> {

    //@Query("SELECT stat FROM Stats stat WHERE stat.app = ?1 AND stat.uri = ?2")
//    List<Stats> findAllByAppAndUri(String app, String uri);
    Stats findByAppAndUri(String app, String uri);

    List<Stats> findAllByDatetimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT stat FROM Stats stat " +
            "JOIN Hit.stat hit" +
            "WHERE stat.uri = ?1 AND stat.uri = ?2")


}
