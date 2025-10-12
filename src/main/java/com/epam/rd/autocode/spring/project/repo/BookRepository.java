package com.epam.rd.autocode.spring.project.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.rd.autocode.spring.project.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByName(String name);
    boolean existsByName(String name);
    void deleteByName(String name);
    Page<Book> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
