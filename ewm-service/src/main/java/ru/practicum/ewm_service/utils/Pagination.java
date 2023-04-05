package ru.practicum.ewm_service.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm_service.exception.BadRequestException;

public class Pagination {
    public static Pageable getPageOrThrow(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("incorrect page parameters");
        }
        return PageRequest.of(from, size);
    }
}
