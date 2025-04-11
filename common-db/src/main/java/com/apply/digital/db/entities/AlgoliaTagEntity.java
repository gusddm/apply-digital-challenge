package com.apply.digital.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "algolia_tags", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlgoliaTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}