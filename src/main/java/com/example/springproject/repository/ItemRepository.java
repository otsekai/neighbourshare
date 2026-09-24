package com.example.springproject.repository;

import com.example.springproject.entity.Item;
import com.example.springproject.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.userId != :currentUserId AND i.owner.banned = false AND i.actualStatus = :status")
    List<Item> findAvailableItemsForUser(
            @Param("currentUserId") Long currentUserId,
            @Param("status") ItemStatus status
    );

    List<Item> findByOwnerUserId(Long currentUserId);
}
