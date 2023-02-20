package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.App;

import java.util.Optional;

public interface AppsRepository extends JpaRepository<App, Integer> {

    Optional<App> getAppByName(String name);
}
