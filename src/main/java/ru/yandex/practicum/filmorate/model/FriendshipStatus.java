package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class FriendshipStatus {
    private Integer id;
    private String name;
    private String displayName; // Неподтверждённая, Подтверждённая
    private String description;

    public FriendshipStatus() {}

    public FriendshipStatus(Integer id, String name, String displayName, String description) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }
}
