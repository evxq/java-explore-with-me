package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndState(Long eventId, EventState eventState);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Set<Event> findAllByIdIn(Set<Long> idSet);

}