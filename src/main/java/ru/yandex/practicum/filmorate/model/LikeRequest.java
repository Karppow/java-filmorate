package ru.yandex.practicum.filmorate.model;

public class LikeRequest {
        private Long filmId;
        private Long userId;

        // Геттер для filmId
        public Long getFilmId() {
            return filmId;
        }

        // Сеттер для filmId
        public void setFilmId(Long filmId) {
            this.filmId = filmId;
        }

        // Геттер для userId
        public Long getUserId() {
            return userId;
        }

        // Сеттер для userId
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
