package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select distinct u from User u where (u.id in :ids)")
    Collection<User> getUsersByIds(List<Integer> ids);

    @Query("select distinct u from User u where (u.id in :ids)")
    Page<User> getUsersByIdsWithPagination(List<Integer> ids, Pageable pageable);

}
