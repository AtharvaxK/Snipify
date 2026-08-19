package com.snipify.snipify.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snipify.snipify.Enums.AuthProviderTypes;
import com.snipify.snipify.Enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_table")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Column(unique = true)
    private String email;
    private String password;

    @ElementCollection(fetch=FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    Set<Roles> roles=new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AuthProviderTypes providerTypes;

    private String providerId;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Pro pro;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Url>urls;



}
