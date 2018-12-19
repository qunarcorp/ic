package com.qunar.cm.ic.controller;

import com.qunar.cm.ic.dto.EventResult;
import com.qunar.cm.ic.dto.EventSaveResult;
import com.qunar.cm.ic.model.Event;
import com.qunar.cm.ic.service.EventService;
import com.qunar.cm.ic.service.ProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Created by yu.qi on 2018/08/27.
 */
@Controller
public class EventController extends AbstractController {
    @Resource
    private EventService eventService;
    @Resource
    private ProducerService producerService;

    @GetMapping("/api/v2/event/{id}")
    @ResponseBody
    Event getEvent(@NotBlank @PathVariable Long id) {
        return eventService.queryById(id);
    }

    @GetMapping("/api/v2/event")
    @ResponseBody
    List<Event> getEventByTimeAndType(@RequestParam String type, @RequestParam String from, @RequestParam(required = false) String to) {
        return eventService.queryByTimeAndType(type, from, to);
    }

    @PostMapping("/api/v2/event")
    @ResponseBody
    EventSaveResult addEvent(@RequestBody Event event) {
        event.setIp(getClientIp());
        //检查ip是否在白名单中
        producerService.checkIp(event.getSource(), getClientIp());
        Event newEvent = eventService.checkAndSaveEvent(event);
        return new EventSaveResult(new EventResult(newEvent.getId()));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/events/{type}")
    @ResponseBody
    EventSaveResult oldAddEvent(
            @NotEmpty
            @PathVariable String type, @RequestBody Event event) {
        event.setIp(getClientIp());

        //成功时返回202而不是200，为了兼容早期的一些生产者而不校验ip
        event.setType(type);
        event.normalizeBody();
        Event newEvent = eventService.checkAndSaveEvent(event);
        return new EventSaveResult(new EventResult(newEvent.getId()));
    }

}
