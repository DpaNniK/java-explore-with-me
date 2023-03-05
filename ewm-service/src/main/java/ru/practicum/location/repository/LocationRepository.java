package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.location.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

}
