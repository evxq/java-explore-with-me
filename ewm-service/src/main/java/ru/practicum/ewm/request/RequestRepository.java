package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByRequesterId(Long userId);

}