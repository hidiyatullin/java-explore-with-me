package ru.practicum.mainService.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.HitClient;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;
    private final HitClient hitClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text, //текст для поиска в содержимом аннотации и подробном описании события
                                         @RequestParam(name = "categories", required = false) List<Long> categoriesId, //список идентификаторов категорий в которых будет вестись поиск
                                         @RequestParam(name = "paid", required = false) Boolean paid, //поиск только платных/бесплатных событий
                                         @RequestParam(name = "rangeStart", required = false) String rangeStart, //дата и время не раньше которых должно произойти событие
                                         @RequestParam(name = "rangeEnd", required = false) String rangeEnd,  //дата и время не позже которых должно произойти событие
                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable, //только события у которых не исчерпан лимит запросов на участие
                                         @RequestParam(name = "sort", required = false) String sort,  //Вариант сортировки: по дате события или по количеству просмотров
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,  //количество событий, которые нужно пропустить для формирования текущего набора
                                         @Positive @RequestParam(name = "size", defaultValue = "10") int size,  //количество событий в наборе
                                         HttpServletRequest httpServletRequest) {
        hitClient.createHit(httpServletRequest);
        return eventService.getEventsWithFilter(text, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id,
                                     HttpServletRequest httpServletRequest) {
        hitClient.createHit(httpServletRequest);
        return eventService.getEventById(id);
    }
}
