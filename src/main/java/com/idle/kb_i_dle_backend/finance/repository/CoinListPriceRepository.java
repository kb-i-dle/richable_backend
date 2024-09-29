package com.idle.kb_i_dle_backend.finance.repository;

import com.idle.kb_i_dle_backend.finance.entity.CoinListPrice;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinListPriceRepository extends JpaRepository<CoinListPrice, Integer> {

}
