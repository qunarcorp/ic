package com.qunar.cm.ic.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qunar.cm.ic.dto.ListenerFetchResult;
import com.qunar.cm.ic.dto.MessageResponse;
import com.qunar.cm.ic.service.ListenerService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by yu.qi on 2018/08/29.
 */
@Controller
@Validated
public class ListenerController extends AbstractController {

    @Resource
    private ListenerService listenerService;

    @GetMapping("/api/v2/event/listener/{token}")
    @ResponseBody
    public ListenerFetchResult getEvents(
            @NotBlank
            @PathVariable String token,
            @RequestParam(required = false) String type,
            @NonNull
            @RequestParam(required = false, defaultValue = "") List<String> types,
            @Max(100)
            @Min(1)
            @NotNull
            @RequestParam(required = false, defaultValue = "100") int maxResults,
            @RequestParam(required = false, defaultValue = "false") boolean longPoll) {
        List<String> targetTypes = Lists.newArrayList(types);
        if(!Strings.isNullOrEmpty(type)) {
            targetTypes.add(type);
        }
        return listenerService.consumeEvents(token, targetTypes, maxResults, longPoll, getClientIp());
    }

    @PostMapping("/api/v2/event/listener/{token}/{code}")
    @ResponseBody
    public MessageResponse acknowledge(
            @NotBlank
            @PathVariable String token,
            @NotBlank
            @PathVariable String code) {
        listenerService.acknowledge(token, code, getClientIp());
        return new MessageResponse("success");
    }
}
