package com.antonio.authserver.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientName;
    private String clientSecret;

    @ManyToOne
    private Realm realm;

    public Client() {
    }

    public Client(String clientName, String clientSecret, Realm realm) {
        this.clientName = clientName;
        this.clientSecret = clientSecret;
        this.realm = realm;
    }
}
