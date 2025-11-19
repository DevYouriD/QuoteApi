package com.kabisa.quote_api.api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rated_quotes", uniqueConstraints = @UniqueConstraint(columnNames = {"quote", "author"}))
public class RatedQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String quote;
    private String author;

    private int totalRating;
    private int ratingCount;

    public double getAverageRating() {
        if (ratingCount == 0) return 0.0;
        return (double) totalRating / ratingCount;
    }

}
