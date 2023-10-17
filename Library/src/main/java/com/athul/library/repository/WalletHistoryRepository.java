package com.athul.library.repository;

import com.athul.library.model.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {

    WalletHistory findById(long id);

    WalletHistory save(WalletHistory walletHistory);

    @Query(value = "select * from wallets_history where wallet_id=:id",nativeQuery = true)
    List<WalletHistory> findAllById(@Param("id") long id);
}
