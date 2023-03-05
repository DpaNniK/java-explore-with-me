package ru.practicum.event;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.state.SortState;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final HttpServletRequest request;


    @GetMapping()
    public Collection<EventShortDto> findPublicEventsWithParameters(@RequestParam(value = "text", required = false)
                                                                    String text,
                                                                    @RequestParam(value = "categories",
                                                                            required = false)
                                                                    Collection<Integer> categories,
                                                                    @RequestParam(value = "paid",
                                                                            required = false) Boolean paid,
                                                                    @RequestParam(value = "rangeStart",
                                                                            required = false)
                                                                    String rangeStart,
                                                                    @RequestParam(value = "rangeEnd", required = false)
                                                                    String rangeEnd,
                                                                    @RequestParam(value = "onlyAvailable",
                                                                            required = false, defaultValue = "false")
                                                                    Boolean onlyAvailable,
                                                                    @RequestParam(value = "sort",
                                                                            defaultValue = "EVENT_DATE") SortState sort,
                                                                    @PositiveOrZero
                                                                    @RequestParam(value = "from", defaultValue = "0")
                                                                    Integer from,
                                                                    @Positive
                                                                    @RequestParam(value = "size", defaultValue = "10")
                                                                    Integer size) {
        return eventService.findPublicEventsWithParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getFullEventById(@PathVariable Integer id) {
        return eventService.getFullEventById(id, request.getRemoteAddr());
    }

}
