package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.Event;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByRequesterId(Long userId);

    @Query("SELECT req FROM Request req " +
            "JOIN req.event ev " +
            "WHERE ev.id = ?1")
    List<Request> findRequestsByInitiator(Long eventId);

    @Query("SELECT COUNT(req) FROM Request req " +
            "WHERE req.event = ?1 " +
            "AND req.status = ?2")
    Long findByEventAndStatus(Event event, RequestStatus status);

    List<Request> findByIdIn(List<Long> idList);

}