package ru.practicum.ewm_service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm_service.event.Event;
import ru.practicum.ewm_service.event.EventRepository;
import ru.practicum.ewm_service.event.EventService;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.utils.Pagination;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public CompilationDto postCompilationByAdmin(NewCompilationDto newCompilationDto) {
        Set<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        List<EventShortDto> eventShortDtos = eventService.createEventShortDtoWithView(new ArrayList<>(events));

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(events);
        compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    public CompilationDto updateCompilationByAdmin(Integer compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = getCompilationOrThrow(compId);
        if (updateRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateRequest.getEvents()));
        }

        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.getTitle() != null && !updateRequest.getTitle().isBlank()) {
            compilation.setTitle(updateRequest.getTitle());
        }

        compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = eventService
                .createEventShortDtoWithView(new ArrayList<>(compilation.getEvents()));

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    public void deleteCompilationByAdmin(Integer compId) {
        getCompilationOrThrow(compId);
        compilationRepository.deleteById(compId);
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = Pagination.getPageOrThrow(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        return compilations.stream().map(compilation -> {
            List<EventShortDto> eventShortDtos = eventService
                    .createEventShortDtoWithView(new ArrayList<>(compilation.getEvents()));
            return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
        }).collect(Collectors.toList());
    }

    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = getCompilationOrThrow(compId);
        List<EventShortDto> eventShortDtos = eventService
                .createEventShortDtoWithView(new ArrayList<>(compilation.getEvents()));

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);

    }

    private Compilation getCompilationOrThrow(Integer id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("competition id N%s", id)));
    }
}
