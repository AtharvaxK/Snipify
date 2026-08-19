package com.snipify.snipify.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snipify.snipify.Enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "url" , indexes = {@Index(name = "expiry_index", columnList = "expiry_time")})
@ToString(exclude = {"clickAnalytics", "user", "pro"})
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Url {

    @Id
    private Long urlId;

    @Column(nullable = false ,columnDefinition = "TEXT")
    private String longUrl;

    @Column(unique = true)
    private String shortCode;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private long clickCount=0L;

    boolean ProUser;



    @OneToMany(mappedBy = "url",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Set<ClickAnalytics> clickAnalytics;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_id")
    @JsonIgnore
    private Pro pro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("urls")
    private User user;




}