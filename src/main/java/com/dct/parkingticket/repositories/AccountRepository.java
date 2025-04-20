package com.dct.parkingticket.repositories;

import com.dct.parkingticket.dto.mapping.IAccountDTO;
import com.dct.parkingticket.dto.mapping.IAuthenticationDTO;
import com.dct.parkingticket.entity.Account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(
        value = "SELECT id, username, fullname, email, phone, status FROM account WHERE status <> 'DELETED'",
        nativeQuery = true
    )
    Page<IAccountDTO> findAllWithPaging(Pageable pageable);

    @Query(
        value = "SELECT id, username, fullname, email, phone, status FROM account WHERE status <> 'DELETED' LIMIT 20",
        nativeQuery = true
    )
    List<IAccountDTO> findAllNonPaging();

    @Query(
        value = """
            SELECT id, email, fullname, device_id as deviceId, status
            FROM account
            WHERE username = ?1 AND status <> 'DELETED'
        """,
        nativeQuery = true
    )
    Optional<IAccountDTO> findByAccountByUsername(String username);

    @Query(
        value = """
            SELECT a.id, a.username, a.password, a.email, a.status, a.device_id as deviceId
            FROM account a WHERE a.username = ?1 AND status <> 'DELETED'
        """,
        nativeQuery = true
    )
    Optional<IAuthenticationDTO> findAuthenticationByUsername(String username);

    @Query(
        value = """
            SELECT a.id, a.username, a.password, a.email, a.status, a.device_id as deviceId
            FROM account a WHERE a.email = ?1 AND status <> 'DELETED'
        """,
        nativeQuery = true
    )
    Optional<IAuthenticationDTO> findAuthenticationByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT COUNT(a.id) FROM Account a WHERE (a.username = ?1 OR a.email = ?2) AND a.id <> ?3")
    long countByUsernameOrEmailAndIdNot(String username, String email, Integer accountId);

    @Modifying
    @Query(value = "UPDATE account SET status = ?2 WHERE id = ?1", nativeQuery = true)
    void updateAccountStatusById(Integer accountId, String status);

    @Modifying
    @Query(value = "UPDATE account SET device_id = ?2 WHERE id = ?1", nativeQuery = true)
    void updateDeviceIdByAccountId(Integer accountId, String deviceId);
}
