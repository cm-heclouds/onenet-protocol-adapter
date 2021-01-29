package com.github.cm.heclouds.adapter.thing.util;

import com.github.cm.heclouds.adapter.core.entity.CallableFuture;
import com.github.cm.heclouds.adapter.core.entity.DeviceResult;
import com.github.cm.heclouds.adapter.core.entity.OneJSONRequest;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.github.cm.heclouds.adapter.thing.schema.Consts;
import com.github.cm.heclouds.adapter.thing.schema.Event;
import com.github.cm.heclouds.adapter.thing.schema.Property;
import com.github.cm.heclouds.adapter.thing.schema.services.DataType;
import com.github.cm.heclouds.adapter.thing.schema.services.Service;
import com.github.cm.heclouds.adapter.thing.schema.services.ServiceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.github.cm.heclouds.adapter.thing.util.CommonUtil.*;

/**
 * 生成方法
 */
public final class MethodUtil {
    private MethodUtil() {
    }

    /**
     * 构建属性上传方法
     */
    public static MethodSpec generatePropertyUploadMethod(Property property) {
        String identifier = property.getIdentifier();
        String functionName = CommonUtil.generateUploadMethodName(identifier, "");
        StringBuilder generateFunction = new StringBuilder("Property.").append(CommonUtil.generateGenerateMethodName(identifier, "Request"));
        String valueName = CommonUtil.generateLowPrefixName(identifier) + "Value";
        generateFunction.append("(");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("上传 " + property.getName() + " 属性\n")
                .addJavadoc("desc: " + property.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(CallableFuture.class), TypeVariableName.get(DeviceResult.class)));
        builder.addParameter(CLASS_DEVICE, "device");

        DataType propertyDataType = property.getDataType();
        String propertyIdentifier = property.getIdentifier();
        switch (propertyDataType.getType()) {
            case Consts.DataType.BOOL:
                builder.addParameter(Boolean.class, valueName);
                break;
            case Consts.DataType.INT32:
            case Consts.DataType.ENUM:
            case Consts.DataType.BITMAP:
                builder.addParameter(Integer.class, valueName);
                break;
            case Consts.DataType.DATE:
            case Consts.DataType.INT64:
                builder.addParameter(Long.class, valueName);
                break;
            case Consts.DataType.FLOAT:
                builder.addParameter(Float.class, valueName);
                break;
            case Consts.DataType.DOUBLE:
                builder.addParameter(Double.class, valueName);
                break;
            case Consts.DataType.STRING:
                builder.addParameter(String.class, valueName);
                break;
            case Consts.DataType.STRUCT:
                propertyIdentifier = property.getIdentifier();
                builder.addParameter(ClassName.get("", "Property." + CommonUtil.generateName(propertyIdentifier)), valueName);
                break;
            case Consts.DataType.ARRAY:
                Object specs = propertyDataType.getSpecs();
                JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                String type = jsonElements.get("type").getAsString();
                TypeName typeName = generateMethodBuilderWithArray(propertyIdentifier, type, "Property.", "");
                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), typeName), valueName);
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        builder.addParameter(Long.class, "time");
        generateFunction.append(valueName).append(", time)");
        builder.addStatement("return adapterApi.uploadProperty(device, $L)", generateFunction.toString());
        return builder.build();
    }

    /**
     * 构建事件上报方法
     */
    public static MethodSpec generateEventUploadMethod(Event event) {
        List<Property> outputData = event.getOutputData();
        String identifier = event.getIdentifier();
        String functionName = CommonUtil.generateUploadMethodName(identifier, "");
        StringBuilder generateFunction = new StringBuilder("Event.").append(CommonUtil.generateGenerateMethodName(identifier, "Request"));
        generateFunction.append("(");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("上报 " + event.getName() + " 事件\n")
                .addJavadoc("desc: " + event.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(CallableFuture.class), TypeVariableName.get(DeviceResult.class)));
        builder.addParameter(CLASS_DEVICE, "device");

        for (Property property : outputData) {
            DataType propertyDataType = property.getDataType();
            String propertyIdentifier = property.getIdentifier();
            String valueName = CommonUtil.generateLowPrefixName(propertyIdentifier) + "Value";
            switch (propertyDataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName);
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    builder.addParameter(Long.class, valueName);
                    break;
                case Consts.DataType.FLOAT:
                    builder.addParameter(Float.class, valueName);
                    break;
                case Consts.DataType.DOUBLE:
                    builder.addParameter(Double.class, valueName);
                    break;
                case Consts.DataType.STRING:
                    builder.addParameter(String.class, valueName);
                    break;
                case Consts.DataType.STRUCT:
                    propertyIdentifier = property.getIdentifier();
                    String parameterValue = CommonUtil.generateLowPrefixName(propertyIdentifier);
                    builder.addParameter(ClassName.get("", "Event." + CommonUtil.generateName(propertyIdentifier) + ""), parameterValue);
                    valueName = parameterValue;
                    break;
                case Consts.DataType.ARRAY:
                    Object specs = propertyDataType.getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    String type = jsonElements.get("type").getAsString();
                    TypeName typeName = generateMethodBuilderWithArray(propertyIdentifier, type, "Event.", "");
                    builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), typeName), valueName);
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                    System.exit(0);
            }
            generateFunction.append(valueName).append(", ");
        }
        builder.addParameter(Long.class, "time");
        generateFunction.append("time)");
        builder.addStatement("return adapterApi.uploadEvent(device, $L)", generateFunction.toString());
        return builder.build();
    }

    /**
     * 构建服务回复方法 (值分开)
     */
    public static MethodSpec generateServiceReplyMethod(Service service) {
        List<ServiceData> output = service.getOutput();
        String identifier = service.getIdentifier();
        String functionName = CommonUtil.generateReplyMethodName(identifier, "");
        StringBuilder generateFunction = new StringBuilder("Service.").append(CommonUtil.generateGenerateMethodName(identifier, "Response"));
        generateFunction.append("(id, code, msg");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("回复 " + service.getName() + " 服务调用\n")
                .addJavadoc("desc: " + service.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);
        builder.addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(Integer.class, "code")
                .addParameter(String.class, "msg");


        for (ServiceData serviceData : output) {
            DataType propertyDataType = serviceData.getDataType();
            String propertyIdentifier = serviceData.getIdentifier();
            String valueName = CommonUtil.generateLowPrefixName(propertyIdentifier) + "Value";
            switch (propertyDataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName);
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    builder.addParameter(Long.class, valueName);
                    break;
                case Consts.DataType.FLOAT:
                    builder.addParameter(Float.class, valueName);
                    break;
                case Consts.DataType.DOUBLE:
                    builder.addParameter(Double.class, valueName);
                    break;
                case Consts.DataType.STRING:
                    builder.addParameter(String.class, valueName);
                    break;
                case Consts.DataType.STRUCT:
                    propertyIdentifier = serviceData.getIdentifier();
                    String parameterValue = CommonUtil.generateLowPrefixName(propertyIdentifier);
                    builder.addParameter(ClassName.get("", "Service." + CommonUtil.generateName(identifier) + ".Output." + generateName(propertyIdentifier)), parameterValue);
                    valueName = parameterValue;
                    break;
                case Consts.DataType.ARRAY:
                    parameterValue = CommonUtil.generateLowPrefixName(propertyIdentifier) + "List";
                    valueName = parameterValue;
                    Object specs = propertyDataType.getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    String type = jsonElements.get("type").getAsString();
                    TypeName typeName = generateMethodBuilderWithArray(propertyIdentifier, type, "Service.", "");
                    builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), typeName), parameterValue);
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                    System.exit(0);
            }
            generateFunction.append(", ").append(valueName);
        }
        generateFunction.append(")");
        builder.addStatement("return adapterApi.replyServiceInvokeRequest(device, $L, $S)", generateFunction.toString(), identifier);
        return builder.build();
    }

    /**
     * 构建服务回复方法
     */
    public static MethodSpec generateServiceReplyMethodWithClass(Service service) {
        String identifier = service.getIdentifier();
        String var = generateLowPrefixName(identifier);
        String innerClassName = "Service." + CommonUtil.generateName(identifier) + ".Output";
        String functionName = CommonUtil.generateReplyMethodName(identifier, "");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("回复 " + service.getName() + " 服务调用\n")
                .addJavadoc("desc: " + service.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class);
        builder.addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(Integer.class, "code")
                .addParameter(String.class, "msg")
                .addParameter(ClassName.get("", innerClassName), var);
        builder.addStatement("return adapterApi.replyServiceInvokeRequest(device, new Response(id, code, msg, $L.encode()), $S)", var, identifier);
        return builder.build();
    }

    /**
     * 构建物模型下发消息接收方法
     */
    public static MethodSpec generatePropertySetOnRequestMethod(CodeBlock.Builder forEachBlock, Property property, String
            thingClassName) {
        String identifier = property.getIdentifier();
        String methodName = CommonUtil.generateOnReceiveMethodName(identifier);
        forEachBlock.add("case $S:\n", identifier)
                .indent();
        thingClassName = thingClassName + ".Property";
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addJavadoc("收到 " + property.getName() + " 消息\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version");

        DataType propertyDataType = property.getDataType();
        switch (propertyDataType.getType()) {
            case Consts.DataType.BOOL:
                builder.addParameter(Boolean.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsBoolean())");
                break;
            case Consts.DataType.INT32:
            case Consts.DataType.ENUM:
            case Consts.DataType.BITMAP:
                builder.addParameter(Integer.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsInt())");
                break;
            case Consts.DataType.DATE:
            case Consts.DataType.INT64:
                builder.addParameter(Long.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsLong())");
                break;
            case Consts.DataType.FLOAT:
                builder.addParameter(Float.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsFloat())");
                break;
            case Consts.DataType.DOUBLE:
                builder.addParameter(Double.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsDouble())");
                break;
            case Consts.DataType.STRING:
                builder.addParameter(String.class, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, value.getAsString())");
                break;
            case Consts.DataType.STRUCT:
                ClassName structClass = ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, thingClassName + "." + CommonUtil.generateName(identifier));
                builder.addParameter(structClass, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, $T.decode(value.getAsJsonObject()))", structClass);
                break;
            case Consts.DataType.ARRAY:
                Object specs = propertyDataType.getSpecs();
                JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                String type = jsonElements.get("type").getAsString();
                TypeName typeName = null;
                ClassName className = null;
                String decodeStr = null;
                if (type.equals(Consts.DataType.STRUCT)) {
                    typeName = TypeVariableName.get(thingClassName + "." + CommonUtil.generateName(identifier));
                    className = ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, thingClassName + "." + CommonUtil.generateName(identifier));
                    decodeStr = "getAsJsonObject()";
                    forEachBlock.addStatement("$T array = value.getAsJsonArray()", JsonArray.class)
                            .addStatement("$T<$T> list = new $T()", List.class, className, ArrayList.class)
                            .beginControlFlow("for ($T jsonElement : array)", JsonElement.class)
                            .addStatement("list.add($T.decode(jsonElement." + decodeStr + "))", ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, thingClassName + "." + CommonUtil.generateName(identifier)))
                            .endControlFlow()
                            .addStatement(methodName + "(device, id, version, list)");
                } else {
                    switch (type) {
                        case Consts.DataType.BOOL:
                            typeName = TypeVariableName.get(Boolean.class);
                            className = ClassName.get(Boolean.class);
                            decodeStr = "getAsBoolean()";
                            break;
                        case Consts.DataType.INT32:
                        case Consts.DataType.ENUM:
                        case Consts.DataType.BITMAP:
                            typeName = TypeVariableName.get(Integer.class);
                            className = ClassName.get(Integer.class);
                            decodeStr = "getAsInt()";
                            break;
                        case Consts.DataType.DATE:
                        case Consts.DataType.INT64:
                            typeName = TypeVariableName.get(Long.class);
                            className = ClassName.get(Long.class);
                            decodeStr = "getAsLong()";
                            break;
                        case Consts.DataType.FLOAT:
                            typeName = TypeVariableName.get(Float.class);
                            className = ClassName.get(Float.class);
                            decodeStr = "getAsFloat()";
                            break;
                        case Consts.DataType.DOUBLE:
                            typeName = TypeVariableName.get(Double.class);
                            className = ClassName.get(Double.class);
                            decodeStr = "getAsDouble()";
                            break;
                        case Consts.DataType.STRING:
                            typeName = TypeVariableName.get(String.class);
                            className = ClassName.get(String.class);
                            decodeStr = "getAsString()";
                            break;
                        default:
                            System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                            System.exit(0);
                    }
                    forEachBlock.addStatement("$T array = value.getAsJsonArray()", JsonArray.class)
                            .addStatement("$T<$T> list = new $T()", List.class, className, ArrayList.class)
                            .beginControlFlow("for ($T jsonElement : array)", JsonElement.class)
                            .addStatement("list.add(jsonElement." + decodeStr + ")")
                            .endControlFlow()
                            .addStatement(methodName + "(device, id, version, list)");
                }
                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), typeName), "value");
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        forEachBlock.addStatement("break")
                .unindent();
        return builder.build();
    }

    /**
     * 构建物模型服务调用消息接收方法
     */
    public static MethodSpec generateServiceInvokeOnRequestMethod(CodeBlock.Builder forEachBlock, Service service, String
            thingClassName) {
        String identifier = service.getIdentifier();
        String serviceClassName = generateName(identifier);
        String serviceClassVar = generateLowPrefixName(identifier);
        String methodName = CommonUtil.generateOnReceiveMethodName(identifier);
        thingClassName = thingClassName + ".Service";
        ClassName fullServiceClassName = ClassName.get(API_EXTENSION_PACKAGE, thingClassName + "." + serviceClassName + ".Input");
        forEachBlock.add("case $S:\n", identifier)
                .indent();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addJavadoc("收到 " + service.getName() + " 服务调用\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "identifier")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version")
                .addParameter(fullServiceClassName, serviceClassVar);
        String statement = methodName + "(device, identifier, id, version" + ", " +
                thingClassName + "." + serviceClassName + ".Input" +
                ".decode(params))";
        forEachBlock
                .addStatement(statement)
                .addStatement("break").unindent();
        return builder.build();
    }

    /**
     * 构建属性生成方法
     */
    public static MethodSpec generatePropertyGenerateMethods(TypeSpec.Builder classBuilder, Property property) {
        String identifier = property.getIdentifier();
        String identifierReplace = CommonUtil.generateLowPrefixName(identifier) + "JsonObject";
        String functionName = CommonUtil.generateGenerateMethodName(identifier, "Request");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + property.getName() + " 属性\n")
                .addJavadoc("desc: " + property.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(OneJSONRequest.class);

        ModelUtil.generateProperties(classBuilder, builder, property, identifierReplace);

        builder.addStatement("return new $T(param)", OneJSONRequest.class);
        return builder.build();
    }

    /**
     * 构建事件生成方法
     */
    public static MethodSpec generateEventGenerateMethod(TypeSpec.Builder classBuilder, Event event) {
        String identifier = event.getIdentifier();
        String functionName = CommonUtil.generateGenerateMethodName(identifier, "Request");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + event.getName() + " 事件\n")
                .addJavadoc("desc: " + event.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(OneJSONRequest.class);

        ModelUtil.generateEvents(classBuilder, builder, event);

        builder.addStatement("return new $T(param)", OneJSONRequest.class);
        return builder.build();
    }

    /**
     * 构建属性生成方法
     */
    public static MethodSpec generatePropertyJsonObjectGenerateMethods(TypeSpec.Builder classBuilder, Property property) {
        String identifier = property.getIdentifier();
        String functionName = CommonUtil.generateGenerateJsonObjectMethodName(identifier, "");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + property.getName() + " 属性的JsonObject\n")
                .addJavadoc("desc: " + property.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(JsonObject.class);
        String propertyValue = generateLowPrefixName(identifier) + "Value";
        DataType propertyDataType = property.getDataType();
        String valueName;
        builder.addStatement("$T param = new JsonObject()", JsonObject.class);

        switch (propertyDataType.getType()) {
            case Consts.DataType.BOOL:
                builder.addParameter(Boolean.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.INT32:
            case Consts.DataType.ENUM:
            case Consts.DataType.BITMAP:
                builder.addParameter(Integer.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.DATE:
            case Consts.DataType.INT64:
                builder.addParameter(Long.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.FLOAT:
                builder.addParameter(Float.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.DOUBLE:
                builder.addParameter(Double.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.STRING:
                builder.addParameter(String.class, "value")
                        .addStatement("param.addProperty(\"value\", value)");
                break;
            case Consts.DataType.STRUCT:
                String propertyStructName = generateName(identifier);
                String propertyValueName = generateLowPrefixName(identifier);
                String structValueJsonObjectName = propertyValueName + "JsonObject";
                ModelUtil.generateStructClass(classBuilder, property, "");
                builder.addParameter(ClassName.get("", propertyStructName), propertyValueName)
                        .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, propertyValueName);
                valueName = structValueJsonObjectName;
                builder.addStatement("param.add(\"value\", $L)", valueName);
                break;
            case Consts.DataType.ARRAY:
                Object specs = propertyDataType.getSpecs();
                JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                String type = jsonElements.get("type").getAsString();
                propertyValueName = generateLowPrefixName(identifier) + "List";
                valueName = propertyValueName + "Value";
                String jsonArrayName = propertyValueName + "JsonArray";
                if (type.equals(Consts.DataType.STRUCT)) {
                    String structName = generateName(identifier);
                    ModelUtil.generateStructClass(classBuilder, property, "");
                    builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(structName)), propertyValueName)
                            .addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                            .addCode(CodeBlock.builder()
                                    .beginControlFlow("for ($L obj : $L)", structName, propertyValueName)
                                    .addStatement("$L.add(obj.encode())", jsonArrayName)
                                    .endControlFlow().build());
                } else {
                    switch (type) {
                        case Consts.DataType.BOOL:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Boolean.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", Boolean.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        case Consts.DataType.INT32:
                        case Consts.DataType.ENUM:
                        case Consts.DataType.BITMAP:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Integer.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", Integer.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        case Consts.DataType.DATE:
                        case Consts.DataType.INT64:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Long.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", Long.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        case Consts.DataType.FLOAT:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Float.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", Float.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        case Consts.DataType.DOUBLE:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Double.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", Double.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        case Consts.DataType.STRING:
                            builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(String.class)), valueName);
                            builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                    .addCode(CodeBlock.builder()
                                            .beginControlFlow("for ($T obj : $L)", String.class, valueName)
                                            .addStatement("$L.add(obj)", jsonArrayName)
                                            .endControlFlow().build());
                            break;
                        default:
                            System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                            System.exit(0);
                    }
                }
                builder.addStatement("param.add(\"value\", $L)", jsonArrayName);
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        builder.addParameter(Long.class, "time")
                .addStatement("param.addProperty(\"time\", time)")
                .addStatement("return param");
        return builder.build();
    }

    /**
     * 构建事件生成方法
     */
    public static MethodSpec generateEventJsonObjectMethod(TypeSpec.Builder classBuilder, Event event) {
        String identifier = event.getIdentifier();
        String functionName = CommonUtil.generateGenerateJsonObjectMethodName(identifier, "");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + event.getName() + " 事件的JsonObject\n")
                .addJavadoc("desc: " + event.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(JsonObject.class);

        List<Property> properties = event.getOutputData();
        builder.addStatement("$T param = new JsonObject()", JsonObject.class)
                .addStatement("$T value = new JsonObject()", JsonObject.class);

        for (Property property : properties) {
            String propertyIdentifier = property.getIdentifier();
            String valueName = generateLowPrefixName(propertyIdentifier) + "Value";
            DataType dataType = property.getDataType();
            boolean isStructOrArray = false;
            switch (dataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName);
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    builder.addParameter(Long.class, valueName);
                    break;
                case Consts.DataType.FLOAT:
                    builder.addParameter(Float.class, valueName);
                    break;
                case Consts.DataType.DOUBLE:
                    builder.addParameter(Double.class, valueName);
                    break;
                case Consts.DataType.STRING:
                    builder.addParameter(String.class, valueName);
                    break;
                case Consts.DataType.STRUCT:
                    isStructOrArray = true;
                    String eventStructName = generateName(propertyIdentifier);
                    String eventValueName = generateLowPrefixName(propertyIdentifier);
                    String structValueJsonObjectName = eventValueName + "ValueJsonObject";
                    ModelUtil.generateStructClass(classBuilder, property, "");
                    builder.addParameter(ClassName.get("", eventStructName), valueName)
                            .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, valueName)
                            .addStatement("value.add($S, $L)", propertyIdentifier, structValueJsonObjectName);
                    break;
                case Consts.DataType.ARRAY:
                    isStructOrArray = true;
                    Object specs = dataType.getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    String type = jsonElements.get("type").getAsString();
                    eventValueName = generateLowPrefixName(propertyIdentifier) + "List";
                    String arrayName = eventValueName + "Value";
                    String jsonArrayName = eventValueName + "JsonArray";
                    valueName = arrayName;
                    if (type.equals(Consts.DataType.STRUCT)) {
                        String structName = generateName(propertyIdentifier);
                        ModelUtil.generateStructClass(classBuilder, property, "");
                        builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(structName)), eventValueName)
                                .addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("for ($L obj : $L)", structName, eventValueName)
                                        .addStatement("$L.add(obj.encode())", jsonArrayName)
                                        .endControlFlow().build());
                    } else {
                        switch (type) {
                            case Consts.DataType.BOOL:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Boolean.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Boolean.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.INT32:
                            case Consts.DataType.ENUM:
                            case Consts.DataType.BITMAP:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Integer.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Integer.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.DATE:
                            case Consts.DataType.INT64:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Long.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Long.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.FLOAT:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Float.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Float.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.DOUBLE:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Double.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Double.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.STRING:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(String.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", String.class, valueName)
                                                .addStatement("$L.add(obj)", jsonArrayName)
                                                .endControlFlow().build());
                                break;
                            default:
                                System.out.println("load thing model file failed, illegal dataType.type:" + dataType.getType());
                                System.exit(0);
                        }
                    }
                    builder.addStatement("value.add($S, $L)", propertyIdentifier, jsonArrayName);
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + dataType.getType());
                    System.exit(0);
            }
            if (!isStructOrArray) {
                builder.addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
            }
        }
        builder.addParameter(Long.class, "time")
                .addStatement("param.add(\"value\", value)")
                .addStatement("param.addProperty(\"time\", time)")
                .addStatement("return param");
        return builder.build();
    }


    /**
     * 构建服务类
     */
    public static MethodSpec generateServicesOutputGenerateMethods(Service service) {
        String identifier = service.getIdentifier();
        String functionName = CommonUtil.generateGenerateMethodName(identifier, "Response");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + service.getName() + " 服务调用响应\n")
                .addJavadoc("desc: " + service.getDesc() + "\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(Response.class);
        builder.addParameter(String.class, "id")
                .addParameter(Integer.class, "code")
                .addParameter(String.class, "msg");

        ModelUtil.generateServiceOutput(builder, service);

        builder.addStatement("return new $T(id, code, msg, $L)", Response.class, generateLowPrefixName(identifier));
        return builder.build();
    }

    private static TypeName generateMethodBuilderWithArray(String propertyIdentifier, String type, String structPrefix, String structSuffix) {
        TypeName typeName;
        Class typeClass = null;
        if (type.equals(Consts.DataType.STRUCT)) {
            typeName = TypeVariableName.get(structPrefix + CommonUtil.generateName(propertyIdentifier) + structSuffix);
        } else {
            switch (type) {
                case Consts.DataType.BOOL:
                    typeClass = Boolean.class;
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    typeClass = Integer.class;
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    typeClass = Long.class;
                    break;
                case Consts.DataType.FLOAT:
                    typeClass = Float.class;
                    break;
                case Consts.DataType.DOUBLE:
                    typeClass = Double.class;
                    break;
                case Consts.DataType.STRING:
                    typeClass = String.class;
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + type);
                    System.exit(0);
            }
            typeName = TypeVariableName.get(typeClass);
        }
        return typeName;
    }
}
