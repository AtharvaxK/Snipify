    package com.snipify.snipify.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;

    @Entity
    @Getter
    @Setter
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class Pro {

        @Id
        private Long id;

        @Column(unique = true)
        private String subdomainName;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        @MapsId
        @JsonIgnore
        private User user;

        @OneToMany(mappedBy = "pro",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JsonIgnore
        private List<Url> urls;
    }
