package com.airbus.retex.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface IRepository<T, I> extends JpaRepository<T, I>, PagingAndSortingRepository<T, I> {
}
