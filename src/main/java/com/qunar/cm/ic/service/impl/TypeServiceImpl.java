package com.qunar.cm.ic.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.dao.TypeRepository;
import com.qunar.cm.ic.model.Event;
import com.qunar.cm.ic.model.Type;
import com.qunar.cm.ic.service.PropertyService;
import com.qunar.cm.ic.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by dandan.sha on 2018/08/29.
 */
@Service
public class TypeServiceImpl implements TypeService {
    private static Logger logger = LoggerFactory.getLogger(TypeServiceImpl.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ObjectMapper schemaObjectMapper = new ObjectMapper().addMixIn(Type.class, Type.SchemaMixIn.class);
    private static JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    private static final String CACHE_KEY = "cache.schema";
    private static final String BASE_TYPE_FILE_NAME = "base-type.json";
    private Type baseType;

    @Resource
    private TypeRepository typeRepository;
    @Resource
    private PropertyService propertyService;

    private volatile Map<String, JsonSchema> caches;

    public TypeServiceImpl() {
        initializeBaseType();
    }

    private void initializeBaseType() {
        URL resource = getClass().getClassLoader().getResource(BASE_TYPE_FILE_NAME);
        Preconditions.checkNotNull(resource, BASE_TYPE_FILE_NAME + "文件不存在");
        try {
            baseType = objectMapper.readValue(resource, Type.class);
        } catch (IOException e) {
            throw new ICException("从" + BASE_TYPE_FILE_NAME + "中创建BaseType异常", e);
        }
    }

    @Override
    public void checkEvent(Event event) {
        JsonNode jsonNode;
        try {
            jsonNode = JsonLoader.fromString(objectMapper.writeValueAsString(event.getBody()));
        } catch (IOException e) {
            throw new ICException("序列化或反序列化Json失败", e);
        }
        Preconditions.checkNotNull(caches, "JsonSchemaCache尚未初始化完成");
        JsonSchema schema;
        schema = caches.get(event.getType());
        if (schema == null) {
            throw new ICException("事件" + event.getType() + "未定义");
        }
        ProcessingReport report;
        try {
            report = schema.validate(jsonNode);
        } catch (ProcessingException e) {
            throw new ICException("校验Json格式异常", e);
        }
        if (!report.isSuccess()) {
            throw new ICException(report.toString());
        }

    }

    @Override
    public List<Type> allTypes() {
        return mergeAllWithBaseType(typeRepository.findAll());
    }

    private List<Type> mergeAllWithBaseType(List<Type> types) {
        return types.stream().map(this::mergeWithBaseType).collect(Collectors.toList());
    }

    private Type mergeWithBaseType(Type type) {
        List<String> newRequired = Lists.newArrayList();
        newRequired.addAll(baseType.getRequired());
        newRequired.addAll(type.getRequired());
        type.setRequired(newRequired.stream().distinct().collect(Collectors.toList()));

        type.setAdditionalProperties(type.getAdditionalProperties() == null ?
                baseType.getAdditionalProperties() : type.getAdditionalProperties());

        Map<String, Object> newProperties = Maps.newLinkedHashMap(baseType.getProperties());
        newProperties.putAll(type.getProperties());
        type.setProperties(newProperties);
        return type;
    }


    @Override
    public Type getType(String name) {
        Optional<Type> optionalType = typeRepository.findOneByName(name);
        return mergeWithBaseType(optionalType.orElseThrow(() -> new ICException(ExceptionEnum.PARAMS_INVALID, "不存在事件类型：" + name)));
    }


    @Scheduled(fixedDelay = 5000L)
    public synchronized void refreshOnChanged() {
        if (propertyService.changedSinceLastAccess(CACHE_KEY)) {
            refresh();
        }
    }

    private synchronized void refresh() {
        List<Type> typeList = mergeAllWithBaseType(typeRepository.findAll());
        Map<String, JsonSchema> newCaches = Maps.newHashMap();
        typeList.forEach(type -> newCaches.put(type.getName(), createJsonSchema(type)));
        caches = newCaches;
        logger.info("更新Schema列表成功，更新的Schema数量为{}", typeList.size());
    }

    /**
     * 创建JsonSchema对象
     */
    private JsonSchema createJsonSchema(Type type) {
        String typeJson;
        try {
            typeJson = schemaObjectMapper.writeValueAsString(type);
        } catch (JsonProcessingException e) {
            throw new ICException(ExceptionEnum.DATA_CONVERTER_ERROR, "将对象转化成Json字符串失败：" + type, e);
        }
        JsonNode jsonNode;
        try {
            jsonNode = JsonLoader.fromString(typeJson);
        } catch (IOException e) {
            throw new ICException(ExceptionEnum.DATA_CONVERTER_ERROR, "从字符串中生成Json失败：" + typeJson, e);
        }
        try {
            return factory.getJsonSchema(jsonNode);
        } catch (ProcessingException e) {
            throw new ICException(ExceptionEnum.DATA_CONVERTER_ERROR, "创建JsonSchema失败：" + jsonNode, e);
        }
    }

}
