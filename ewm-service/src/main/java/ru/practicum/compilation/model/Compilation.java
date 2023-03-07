package ru.practicum.compilation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Collection<Event> events;
    @Column(name = "pinned")
    private boolean pinned;
    @Column(name = "title")
    private String title;
}
