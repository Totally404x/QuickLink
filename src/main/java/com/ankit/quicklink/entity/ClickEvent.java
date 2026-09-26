package com.ankit.quicklink.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="click_events")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime clickedAt;
    @ManyToOne
    @JoinColumn(name="link_id", nullable = false)
    @JsonIgnore
    private Link link;

    public ClickEvent() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
