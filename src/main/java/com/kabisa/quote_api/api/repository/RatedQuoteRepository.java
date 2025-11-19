package com.kabisa.quote_api.api.repository;

import com.kabisa.quote_api.api.model.entity.RatedQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatedQuoteRepository extends JpaRepository<RatedQuote, Long> {
    Optional<RatedQuote> findByQuoteAndAuthor(String quote, String author);

    @Query("SELECT r FROM RatedQuote r ORDER BY " +
            "CASE WHEN r.ratingCount = 0 THEN 0 ELSE (r.totalRating * 1.0 / r.ratingCount) END DESC")
    List<RatedQuote> findTopRatedByAverageRating(Pageable pageable);
}
