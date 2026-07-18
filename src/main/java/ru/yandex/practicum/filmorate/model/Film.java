package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    private  String name;
    private String description;
    private String releaseDate;
    private int duration;

}
