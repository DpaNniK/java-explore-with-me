package ru.practicum.subscriber.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.error.RequestError;
import ru.practicum.subscriber.model.Subscriber;
import ru.practicum.subscriber.repository.SubscriberRepository;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriberServiceImpl implements SubscriberService {
    private final SubscriberRepository subscriberRepository;

    @Override
    public void subscribeToUser(User subscriber, User initiator) {
        checkThatUserNotSubscriberForInitiator(subscriber, initiator);
        Subscriber sub = new Subscriber();
        sub.setSubscriber(subscriber);
        sub.setInitiator(initiator);
        subscriberRepository.save(sub);
        log.info("Пользователь {} подписался на пользователя {}", subscriber, initiator);
    }

    @Override
    public void unSubscribeToUser(User subscriber, User initiator) {
        if (!checkThatUserIsSubscriberForInitiator(subscriber, initiator)) {
            log.info("Пользователь {} не может отписаться от пользователя {}, " +
                    "так как он не является его подписчиком", subscriber, initiator);
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно отписаться," +
                    " пользователь " + subscriber + " не является подписчиком " + initiator);
        }
        Subscriber subscriberNow = subscriberRepository
                .getSubscriberBySubscriberAndInitiator(subscriber, initiator);
        subscriberRepository.deleteSubById(subscriberNow.getId());
        log.info("Пользователь {} отписался от пользователя {}", subscriber, initiator);
    }

    //Получаю лист подписчиков для пользователя(Initiator)
    @Override
    public List<Subscriber> getSubscriberListForInitiator(User initiator) {
        log.info("Запрошен список подписчиков пользователя {}", initiator);
        return subscriberRepository.getSubscribersByInitiator(initiator);
    }

    @Override
    public List<Subscriber> getInitiatorListForSubscriber(User subscriber) {
        log.info("Запрошен список подписок пользователя {}", subscriber);
        return subscriberRepository.getSubscribersBySubscriber(subscriber);
    }

    //Проверяю, что пользователь, который хочет подписаться на другого пользователя,
    //Не является его подписчиком
    private void checkThatUserNotSubscriberForInitiator(User sub, User initiator) {
        Collection<Subscriber> subscribers = getSubscriberListForInitiator(initiator);
        subscribers.forEach(subscriber -> {
            if (subscriber.getSubscriber().getId().equals(sub.getId())) {
                log.info("Невозможно подписаться на пользователя {}, " +
                        "пользователь {} уже является подписчиком", initiator, sub);
                throw new RequestError(HttpStatus.CONFLICT, "Невозможно подписаться " +
                        "на пользователя " + initiator + ", пользователь = " + sub +
                        " уже подписчик ");
            }
        });
    }

    private boolean checkThatUserIsSubscriberForInitiator(User sub, User initiator) {
        Collection<Subscriber> subscribers = getSubscriberListForInitiator(initiator);
        for (Subscriber subscriber : subscribers) {
            if (subscriber.getSubscriber().getId().equals(sub.getId())) {
                return true;
            }
        }
        return false;
    }
}
