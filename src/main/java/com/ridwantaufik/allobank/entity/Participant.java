package com.ridwantaufik.allobank.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "participants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_participant_group_name", columnNames = { "bill_group_id", "name" })
})
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_group_id", nullable = false)
    private BillGroup billGroup;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BillGroup getBillGroup() {
        return billGroup;
    }

    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }
}