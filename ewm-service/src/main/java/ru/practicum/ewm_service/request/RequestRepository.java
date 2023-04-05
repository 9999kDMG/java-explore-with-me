package ru.practicum.ewm_service.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByRequesterId(int userId);

    Optional<Request> findByRequesterIdAndEventId(int userId, int eventId);

    @Query("SELECT r " +
            "FROM Request r " +
            "WHERE r.event.initiator.id = :user AND r.event.id = :event AND r.id IN :requests " +
            "ORDER BY r.created")
    List<Request> findAllByParam(@Param("user") Integer userId,
                                 @Param("event") Integer eventId,
                                 @Param("requests") List<Integer> requestIds);

    List<Request> findByEventInitiatorIdAndEventId(Integer userId, Integer eventId);
}
