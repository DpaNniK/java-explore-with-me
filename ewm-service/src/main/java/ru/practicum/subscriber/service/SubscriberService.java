package ru.practicum.subscriber.service;

import ru.practicum.subscriber.model.Subscriber;
import ru.practicum.user.model.User;

import java.util.List;

public interface SubscriberService {

    void subscribeToUser(User subscriber, User initiator);

    void unSubscribeToUser(User subscriber, User initiator);

    List<Subscriber> getSubscriberListForInitiator(User initiator);

    List<Subscriber> getInitiatorListForSubscriber(User subscriber);
}
