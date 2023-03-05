package ru.practicum.request.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.model.Event;
import ru.practicum.state.RequestState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "requests", schema = "public")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "requester")
    @ToString.Exclude
    private User requester;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event")
    @ToString.Exclude
    private Event event;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private RequestState status;
    @DateTimeFormat
    @JoinColumn(name = "created")
    private LocalDateTime created;
}
