package ru.yandex.practicum.filmorate.model;

public class CommonFriendsRequest {
    private Integer id;
    private Integer otherId;

    public CommonFriendsRequest() {
    }

    public CommonFriendsRequest(Integer id, Integer otherId) {
        this.id = id;
        this.otherId = otherId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOtherId() {
        return otherId;
    }

    public void setOtherId(Integer otherId) {
        this.otherId = otherId;
    }

    @Override
    public String toString() {
        return "CommonFriendsRequest{" +
                "id=" + id +
                ", otherId=" + otherId +
                '}';
    }
}
