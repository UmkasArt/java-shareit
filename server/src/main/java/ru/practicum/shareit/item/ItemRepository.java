package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Long ownerId, Pageable pageable);

    Item findItemById(Long id);

    Item findItemByIdAndOwnerId(Long id, Long ownerId);

    void deleteByIdAndOwnerId(Long id, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    @Query(value = "SELECT * FROM ITEMS i " +
            "WHERE i.IS_AVAILABLE = true AND (LOWER(i.NAME) LIKE LOWER(CONCAT('%', ?1,'%')) " +
            "OR LOWER(i.DESCRIPTION) LIKE LOWER(CONCAT('%', ?1,'%')))", nativeQuery = true)
    List<Item> searchItems(String text, Pageable pageable);
}