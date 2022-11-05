package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(Long ownerId);

    Item findItemById(Long id);

    Item findItemByIdAndOwnerId(Long id, Long ownerId);

    void deleteByIdAndOwnerId(Long id, Long ownerId);

    @Query(value = "SELECT * FROM ITEMS i " +
            "WHERE i.IS_AVAILABLE = true AND (LOWER(i.NAME) LIKE LOWER(CONCAT('%', ?1,'%')) " +
            "OR LOWER(i.DESCRIPTION) LIKE LOWER(CONCAT('%', ?1,'%')))", nativeQuery = true)
    List<Item> searchItems(String text);
}