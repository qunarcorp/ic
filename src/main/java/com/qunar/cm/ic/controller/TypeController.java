package com.qunar.cm.ic.controller;

import com.qunar.cm.ic.model.Type;
import com.qunar.cm.ic.service.TypeService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by yu.qi on 2018/09/12.
 */
@Controller
@Validated
public class TypeController extends AbstractController {
    @Resource
    private TypeService typeService;

    @GetMapping("/api/v2/type")
    @ResponseBody
    public List<Type> getTypes() {
        return typeService.allTypes();
    }

    @GetMapping("/api/v2/type/name/{name}")
    @ResponseBody
    public Type getType(@NotBlank
                        @PathVariable String name) {
        return typeService.getType(name);
    }
}
