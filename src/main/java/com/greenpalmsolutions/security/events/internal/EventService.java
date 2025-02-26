package com.greenpalmsolutions.security.events.internal;

import com.greenpalmsolutions.security.accounts.api.behavior.FindCurrentAccount;
import com.greenpalmsolutions.security.accounts.api.behavior.FindUserIdForUsername;
import com.greenpalmsolutions.security.core.errorhandling.InvalidRequestException;
import com.greenpalmsolutions.security.events.api.behavior.*;
import com.greenpalmsolutions.security.events.api.model.*;
import com.greenpalmsolutions.security.schedulekeys.api.behavior.ScheduleKeyIsValidForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class EventService implements CreateEvent, FindEvents, UpdateEvent, DeleteEvent, FindSchedule {

    private final EventRepository eventRepository;
    private final FindCurrentAccount findCurrentAccount;
    private final FindUserIdForUsername findUserIdForUsername;
    private final ScheduleKeyIsValidForUser scheduleKeyIsValidForUser;

    @Override
    public EventDetails createEventForRequest(CreateEventRequest request) {
        String currentUserId = findCurrentAccount.getUserIdForCurrentAccount();
        if (eventRepository.eventExistsBetweenStartDateTimeAndEndDateTimeForUserId(
                request.getStartDateTime(), request.getEndDateTime(), currentUserId)) {
            throw new InvalidRequestException("An event is already scheduled within this timeframe");
        }
        return eventRepository.save(new Event()
                .fromRequestAndUserId(request, currentUserId))
                .getDetails();
    }

    @Override
    public List<EventDetails> findEventsForCurrentUser() {
        return eventRepository.findByUserId(findCurrentAccount.getUserIdForCurrentAccount())
                .stream().map(Event::getDetails).toList();
    }

    @Override
    public EventDetails updateEventForRequest(UpdateEventRequest request) {
        if (eventRepository.eventExistsBetweenStartDateTimeAndEndDateTimeForUserIdAndEventId(
                request.getStartDateTime(),
                request.getEndDateTime(),
                findCurrentAccount.getUserIdForCurrentAccount(),
                request.getEventId())) {
            throw new InvalidRequestException("An event is already scheduled within this timeframe");
        }
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(
                () -> new RuntimeException("Cannot find event to update for ID"));
        event.fromUpdateRequest(request);
        return eventRepository.save(event).getDetails();
    }

    @Override
    public void deleteEventForRequest(DeleteEventRequest request) {
        eventRepository.deleteById(request.getEventId());
    }

    @Override
    public ScheduleDetails findScheduleForRequest(FindScheduleRequest request) {
        String scheduleUserId = findUserIdForUsername.findForUsername(request.getUsername());
        if (scheduleKeyIsNotValidFor(request, scheduleUserId)) {
            throw new InvalidRequestException("Schedule key is not valid for user");
        }
        return new ScheduleDetails(request.getUsername(), eventRepository.findEventDatesForUserId(scheduleUserId));
    }

    private boolean scheduleKeyIsNotValidFor(FindScheduleRequest request, String scheduleUserId) {
        return !scheduleKeyIsValidForUser.scheduleKeyIsValidForUser(
                request.getScheduleKey(), scheduleUserId);
    }
}
