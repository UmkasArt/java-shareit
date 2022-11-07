package ru.practicum.shareit.item.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ITEMS")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME")
    @NotNull
    @NotBlank
    private String name;
    @Column(name = "DESCRIPTION")
    @NotNull
    private String description;
    @Column(name = "IS_AVAILABLE")
    @NotNull
    private Boolean available;
    @Column(name = "OWNER_ID")
    private Long ownerId;
    @Column(name = "REQUEST_ID")
    private Long requestId;
}
