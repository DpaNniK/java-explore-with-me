package ru.practicum.subscriber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.subscriber.model.Subscriber;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {

    List<Subscriber> getSubscribersByInitiator(User initiator);

    List<Subscriber> getSubscribersBySubscriber(User subscriber);

    Subscriber getSubscriberBySubscriberAndInitiator(User subscriber, User initiator);

    @Modifying
    @Transactional
    @Query("delete from Subscriber s where s.id = :id")
    void deleteSubById(@Param("id") Integer id);
}
