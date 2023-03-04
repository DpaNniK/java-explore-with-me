package ru.practicum.event.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventWithRequestNum;
import ru.practicum.state.State;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "events", schema = "public")
@NamedNativeQuery(name = "FindRequestForEventWithStateConfirmed",
        query = "select e.id as eventId, count (r.id) as confirmedRequestSize  " +
                "from requests r join events e on e.id = r.event " +
                "where e.id in :events and r.status = :status " +
                "group by e.id", resultSetMapping = "EventWithRequestNum")
@SqlResultSetMapping(name = "EventWithRequestNum", classes = {
        @ConstructorResult(
                columns = {
                        @ColumnResult(name = "eventId", type = Integer.class),
                        @ColumnResult(name = "confirmedRequestSize", type = Integer.class),
                },
                targetClass = EventWithRequestNum.class
        )
})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "annotation")
    private String annotation;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "category")
    private Category category;
    @DateTimeFormat
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @JoinColumn(name = "description")
    private String description;
    @DateTimeFormat
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "initiator")
    private User initiator;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location")
    private Location location;
    @JoinColumn(name = "paid")
    private boolean paid;
    @JoinColumn(name = "participant_limit")
    private Integer participantLimit;
    @DateTimeFormat
    @JoinColumn(name = "published_on")
    private LocalDateTime publishedOn;
    @JoinColumn(name = "request_moderation")
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "state")
    private State state;
    @JoinColumn(name = "title")
    private String title;
}
