package ru.practicum.mainService.сategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainService.сategory.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(nativeQuery = true,
            value = "select * from categories as c " +
                    "where c.name = ?1 ")
    Category findByName(String name);
}
