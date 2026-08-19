package com.snipify.snipify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snipify.snipify.Enums.Browsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(exclude = "url")
@Table(name = "click_analytics")
public class ClickAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clickId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    @JsonIgnore
    private Url url;

    @CreationTimestamp
    private LocalDateTime clickedAt;

    private String device;

    @Enumerated(value =EnumType.STRING)
    private Browsers browser;

    private String city;
    private String country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClickAnalytics)) return false;
        ClickAnalytics that = (ClickAnalytics) o;
        return clickId != 0 && clickId == that.clickId;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }



}