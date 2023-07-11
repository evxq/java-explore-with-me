package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndState(Long eventId, EventState eventState);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Page<Event> findAllByStateAndEventDateAfter(EventState eventState, LocalDateTime now, Pageable pageable);

    Page<Event> findAllByCategoryIdInAndStateAndEventDateAfter(List<Long> categories, EventState eventState,
                                                               LocalDateTime now, Pageable pageable);

    String QUERY_BY_PARAMETERS = "SELECT ev FROM Event ev " +
            "JOIN ev.category cat " +
            "WHERE UPPER(ev.annotation) LIKE UPPER(CONCAT('%', ?1, '%')) OR UPPER(ev.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "AND cat.id IN ?2 " +
            "AND ev.paid = ?3 " +
            "AND ev.eventDate BETWEEN ?4 and ?5 " +
            "AND ev.state = 'published' ";

    // НЕПРАВИЛЬНО ПОКА НЕ ЗАГРУЖЕНЫ ПРОСМОТРЫ !!!!!!

    @Query(QUERY_BY_PARAMETERS +
            "AND ev.confirmedRequests < ev.participantLimit " +
            "ORDER BY ev.eventDate")
    Page<Event> findAllAvailableByParamsSortByDate(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(QUERY_BY_PARAMETERS +
            "AND ev.confirmedRequests < ev.participantLimit " +
            "ORDER BY ev.views")
    Page<Event> findAllAvailableByParamsSortByViews(String text, List<Long> categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(QUERY_BY_PARAMETERS + "ORDER BY ev.eventDate")
    Page<Event> findAllByParamsSortByDate(String text, List<Long> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(QUERY_BY_PARAMETERS + "ORDER BY ev.views")
    Page<Event> findAllByParamsSortByViews(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT ev FROM Event ev " +
            "JOIN ev.category cat " +
            "WHERE UPPER(ev.annotation) LIKE UPPER(CONCAT('%', ?1, '%')) OR UPPER(ev.description) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "AND cat.id IN ?2 " +
            "AND ev.paid = ?3 " +
            "AND ev.eventDate > ?4 " +
            "AND ev.state = 'published'")
    Page<Event> findAllByParamsWithoutSort(String text, List<Long> categories, Boolean paid, LocalDateTime date, Pageable pageable);

    @Query("SELECT ev FROM Event ev " +
            "JOIN ev.category cat " +
            "JOIN ev.initiator init " +
            "WHERE init.id IN ?1 " +
            "AND ev.state IN ?2 " +
            "AND cat.id IN ?3 " +
            "AND ev.eventDate BETWEEN ?4 and ?5")
    Page<Event> findAllByParametersForAdmin(List<Long> users, List<EventState> stateList, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Set<Event> findAllByIdIn(Set<Long> idSet);

}