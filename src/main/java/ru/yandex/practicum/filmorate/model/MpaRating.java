package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class MpaRating {
    private Integer id;
    private String name;
    private String description;

    public MpaRating() {}

    public MpaRating(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public MpaRating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
