package com.example.campuslostfound.repository;

import com.example.campuslostfound.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByStatus(String status);

    List<Item> findByUserId(Long userId);
}