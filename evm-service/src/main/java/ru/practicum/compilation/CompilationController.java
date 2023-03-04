package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping()
    public Collection<CompilationDto> getCompilationWithParam(@RequestParam(value = "pinned", defaultValue = "false")
                                                              boolean pinned,
                                                              @PositiveOrZero
                                                              @RequestParam(value = "from", defaultValue = "0")
                                                              Integer from,
                                                              @Positive
                                                              @RequestParam(value = "size", defaultValue = "10")
                                                              Integer size) {

        return compilationService.getCompilationWithParam(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Integer compId) {
        return compilationService.getCompilationById(compId);
    }
}
