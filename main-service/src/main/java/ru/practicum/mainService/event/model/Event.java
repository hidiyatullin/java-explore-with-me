package ru.practicum.mainService.event.model;

import lombok.*;
import ru.practicum.mainService.сategory.model.Category;
import ru.practicum.mainService.compilation.model.Compilation;
import ru.practicum.mainService.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotBlank
    @Column(name = "annotation", length = 1024)
    private String annotation;

    @Column(name = "description", length = 2048)
    private String description;

    @ManyToOne
    private Category category;

    @Builder.Default
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "created_on")
    private LocalDateTime createOn;

    @ManyToOne
    private User initiator;

    @Column(name = "paid")
    private Boolean isPaid;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @ManyToOne
    private Location location;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;

    @Column(name = "participant_linit")
    private Integer participantLimit;

    @Column(name = "views")
    private Long views;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id"))
    private List<Compilation> compilations;
}