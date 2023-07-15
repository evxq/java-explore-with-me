package ru.practicum.ewm.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventCustomRepositoryImpl implements EventCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> getEventsByAdmin(List<Long> users, List<EventState> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate criteria = builder.conjunction();

        if (users != null && !users.isEmpty()) {
            criteria = builder.and(criteria, root.get("initiator").in(users));
        }
        if (states != null && !states.isEmpty()) {
            criteria = builder.and(criteria, root.get("state").in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }
        if (rangeStart != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        query.select(root).where(criteria);

        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    @Override
    public List<Event> getEventsByUser(String text, List<Long> categories, Boolean paid,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       Boolean onlyAvailable, String sort, Integer from,
                                       Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Predicate criteria = builder.conjunction();
        criteria = builder.and(criteria, builder.equal(root.get("state"), EventState.PUBLISHED));

        if (text != null && !text.isEmpty()) {
            Predicate ann = builder.like(builder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%");
            Predicate desc = builder.like(builder.upper(root.get("description")), "%" + text.toUpperCase() + "%");
            criteria = builder.and(criteria, builder.or(ann, desc));
        }
        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }
        if (paid != null) {
            criteria = builder.and(criteria, builder.equal(root.get("paid"), paid));
        }
        if (rangeStart != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            criteria = builder.and(criteria, builder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
        }
        if (sort != null && sort.equals("EVENT_DATE")) {
            query = query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));
        }
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }
}
