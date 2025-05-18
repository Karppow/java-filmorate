package ru.yandex.practicum.filmorate.model;

public class Mpa {
    private Integer id;
    private String name;

    // Конструктор с двумя параметрами
    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Сеттеры (если они нужны)
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
