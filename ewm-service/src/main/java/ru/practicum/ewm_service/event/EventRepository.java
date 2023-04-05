package ru.practicum.ewm_service.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm_service.request.RequestDto;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    List<Event> findAllByInitiatorIdOrderByCreatedOnDesc(Integer userId, Pageable pageable);

    Set<Event> findAllByIdIn(List<Integer> events);
}
