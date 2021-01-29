package com.github.cm.heclouds.adapter.thing.util;

import com.github.cm.heclouds.adapter.thing.schema.Consts;
import com.github.cm.heclouds.adapter.thing.schema.Event;
import com.github.cm.heclouds.adapter.thing.schema.Property;
import com.github.cm.heclouds.adapter.thing.schema.services.DataType;
import com.github.cm.heclouds.adapter.thing.schema.services.Service;
import com.github.cm.heclouds.adapter.thing.schema.services.ServiceData;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.github.cm.heclouds.adapter.thing.util.CommonUtil.*;

/**
 * 生成类
 */
public final class ModelUtil {

    private ModelUtil() {
    }

    public static void generateProperties(TypeSpec.Builder classBuilder, MethodSpec.Builder builder, Property property, String identifierReplace) {
        DataType propertyDataType = property.getDataType();
        String propertyIdentifier = property.getIdentifier();
        String valueName;
        builder.addStatement("$T param = new JsonObject()", JsonObject.class)
                .addStatement("$T $L = new JsonObject()", JsonObject.class, identifierReplace)
                .addStatement("param.add($S, $L)", propertyIdentifier, identifierReplace);
        switch (propertyDataType.getType()) {
            case Consts.DataType.BOOL:
                builder.addParameter(Boolean.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.INT32:
            case Consts.DataType.ENUM:
            case Consts.DataType.BITMAP:
                builder.addParameter(Integer.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.DATE:
            case Consts.DataType.INT64:
                builder.addParameter(Long.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.FLOAT:
                builder.addParameter(Float.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.DOUBLE:
                builder.addParameter(Double.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.STRING:
                builder.addParameter(String.class, "value")
                        .addStatement("$L.addProperty(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.STRUCT:
                String propertyArrayName = generateName(propertyIdentifier);
                String propertyValueName = generateLowPrefixName(propertyIdentifier);
                String structValueJsonObjectName = propertyValueName + "Value";
                generateStructClass(classBuilder, property, "");
                builder.addParameter(ClassName.get("", propertyArrayName), propertyValueName)
                        .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, propertyValueName);
                valueName = structValueJsonObjectName;
                builder.addStatement("$L.add(\"value\", $L)", identifierReplace, valueName);
                break;
            case Consts.DataType.ARRAY:
                Object specs = propertyDataType.getSpecs();
                JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                String type = jsonElements.get("type").getAsString();
                propertyValueName = generateLowPrefixName(propertyIdentifier) + "List";
                valueName = propertyValueName + "Value";
                String jsonArrayName = propertyValueName + "JsonArray";
                if (type.equals(Consts.DataType.STRUCT)) {
                    String structName = generateName(propertyIdentifier);
                    generateStructClass(classBuilder, property, "");
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
                builder.addStatement("$L.add(\"value\", $L)", identifierReplace, jsonArrayName);
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        builder.addParameter(Long.class, "time")
                .addStatement("$L.addProperty(\"time\", time)", identifierReplace);
    }

    public static void generateEvents(TypeSpec.Builder classBuilder, MethodSpec.Builder builder, Event event) {
        List<Property> properties = event.getOutputData();
        String identifier = event.getIdentifier();
        String eventValue = generateLowPrefixName(identifier);
        builder.addStatement("$T param = new JsonObject()", JsonObject.class)
                .addStatement("$T $L = new JsonObject()", JsonObject.class, eventValue)
                .addStatement("param.add($S, $L)", identifier, eventValue)
                .addStatement("$T value = new JsonObject()", JsonObject.class);

        for (Property property : properties) {
            String propertyIdentifier = property.getIdentifier();
            String valueName = generateLowPrefixName(propertyIdentifier) + "Value";
            DataType dataType = property.getDataType();
            switch (dataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    builder.addParameter(Long.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.FLOAT:
                    builder.addParameter(Float.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.DOUBLE:
                    builder.addParameter(Double.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.STRING:
                    builder.addParameter(String.class, valueName)
                            .addStatement("value.addProperty($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.STRUCT:
                    String eventStructName = generateName(propertyIdentifier);
                    String eventValueName = generateLowPrefixName(propertyIdentifier);
                    String structValueJsonObjectName = eventValueName + "ValueJsonObject";
                    generateStructClass(classBuilder, property, "");
                    builder.addParameter(ClassName.get("", eventStructName), valueName)
                            .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, valueName);
                    builder.addStatement("value.add($S, $L)", propertyIdentifier, structValueJsonObjectName);
                    break;
                case Consts.DataType.ARRAY:
                    Object specs = dataType.getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    String type = jsonElements.get("type").getAsString();
                    eventValueName = generateLowPrefixName(propertyIdentifier) + "List";
                    String arrayName = eventValueName + "Value";
                    String jsonArrayName = eventValueName + "JsonArray";
                    valueName = arrayName;
                    if (type.equals(Consts.DataType.STRUCT)) {
                        String structName = generateName(propertyIdentifier);
                        generateStructClass(classBuilder, property, "");
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
        }

        builder.addParameter(Long.class, "time")
                .addStatement("$L.add(\"value\", value)", eventValue)
                .addStatement("$L.addProperty(\"time\", time)", eventValue);
    }

    public static void generateServiceOutput(MethodSpec.Builder builder, Service service) {
        List<ServiceData> output = service.getOutput();
        String identifier = service.getIdentifier();
        String serviceValue = generateLowPrefixName(identifier);
        builder.addStatement("$T $L = new JsonObject()", JsonObject.class, serviceValue);

        for (ServiceData serviceData : output) {
            String dataIdentifier = serviceData.getIdentifier();
            String valueName = generateLowPrefixName(dataIdentifier) + "Value";
            DataType dataType = serviceData.getDataType();
            switch (dataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    builder.addParameter(Long.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.FLOAT:
                    builder.addParameter(Float.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.DOUBLE:
                    builder.addParameter(Double.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.STRING:
                    builder.addParameter(String.class, valueName)
                            .addStatement(serviceValue + ".addProperty($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.STRUCT:
                    String serviceClassName = "Service." + generateName(identifier);
                    String structName = generateName(dataIdentifier);
                    String structValueName = generateLowPrefixName(dataIdentifier);
                    String structValueJsonObjectName = structValueName + "Value";
                    builder.addParameter(ClassName.get("", serviceClassName + ".Output." + structName), structValueName)
                            .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, structValueName);
                    valueName = structValueJsonObjectName;
                    builder.addStatement(serviceValue + ".add($S, $L)", dataIdentifier, valueName);
                    break;
                case Consts.DataType.ARRAY:
                    Property property = new Property();
                    property.setIdentifier(dataIdentifier);
                    property.setName(service.getName());
                    property.setDataType(dataType);
                    Object specs = property.getDataType().getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    String type = jsonElements.get("type").getAsString();
                    String serviceArrayName = "Service." + generateName(identifier) + ".Output";
                    String serviceValueName = generateLowPrefixName(dataIdentifier) + "List";
                    String arrayName = serviceValueName + "Value";
                    if (type.equals(Consts.DataType.STRUCT)) {
                        builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(arrayName)), serviceValueName)
                                .addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("for ($L obj : $L)", serviceArrayName, serviceValueName)
                                        .addStatement(serviceValue + ".add(obj.encode())")
                                        .endControlFlow().build());
                    } else {
                        valueName = dataIdentifier + "List";
                        switch (type) {
                            case Consts.DataType.BOOL:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Boolean.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Boolean.class, valueName)
                                                .addStatement(serviceValue + ".add(obj)")
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.INT32:
                            case Consts.DataType.ENUM:
                            case Consts.DataType.BITMAP:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Integer.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Integer.class, valueName)
                                                .addStatement("$L.add(obj)", arrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.DATE:
                            case Consts.DataType.INT64:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Long.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Long.class, valueName)
                                                .addStatement("$L.add(obj)", arrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.FLOAT:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Float.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Float.class, valueName)
                                                .addStatement("$L.add(obj)", arrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.DOUBLE:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(Double.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", Double.class, valueName)
                                                .addStatement("$L.add(obj)", arrayName)
                                                .endControlFlow().build());
                                break;
                            case Consts.DataType.STRING:
                                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(String.class)), valueName);
                                builder.addStatement("$T $L = new JsonArray()", JsonArray.class, arrayName)
                                        .addCode(CodeBlock.builder()
                                                .beginControlFlow("for ($T obj : $L)", String.class, valueName)
                                                .addStatement("$L.add(obj)", arrayName)
                                                .endControlFlow().build());
                                break;
                            default:
                                System.out.println("load thing model file failed, illegal dataType.type:" + dataType.getType());
                                System.exit(0);
                        }
                    }
                    builder.addStatement(serviceValue + ".add($S, $L)", dataIdentifier, arrayName);
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + dataType.getType());
                    System.exit(0);
            }
        }
    }

    public static void generateStructClass(TypeSpec.Builder classBuilder, Property property, String suffix) {
        List<MethodSpec.Builder> getterMethods = new ArrayList<>();
        List<MethodSpec.Builder> setterMethods = new ArrayList<>();
        List<String> decodeStatement = new ArrayList<>();

        String identifier = property.getIdentifier();
        String nestedClassName = generateName(identifier) + suffix;
        TypeSpec.Builder structClassBuilder = TypeSpec.classBuilder(nestedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addJavadoc("$L Struct 类\n", property.getName());

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder encodeMethodBuilder = MethodSpec.methodBuilder("encode")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T jsonObject = new JsonObject()", JsonObject.class)
                .returns(JsonObject.class);

        MethodSpec.Builder decodeMethodBuilder = MethodSpec.methodBuilder("decode")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(JsonObject.class, "jsonObject")
                .returns(ClassName.get("", nestedClassName));

        MethodSpec.Builder toStringMethodBuilder = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("java.lang", "Override"))
                .returns(String.class);
        StringBuilder toStringExpression = new StringBuilder("\"{" + nestedClassName + ":\" +\n");
        int parameterCount = 0;

        Object specs = property.getDataType().getSpecs();
        JsonArray specArray;
        try {
            specArray = GSON.fromJson(GSON.toJson(specs), JsonArray.class);
        } catch (JsonSyntaxException e) {
            JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
            specArray = jsonElements.getAsJsonArray("specs");
        }
        for (JsonElement jsonElement : specArray) {
            Property prop = GSON.fromJson(jsonElement, Property.class);
            String propIdentifier = prop.getIdentifier();
            String parameter = propIdentifier.replaceAll("-", "");
            String type = prop.getDataType().getType();
            switch (type) {
                case Consts.DataType.BOOL:
                    structClassBuilder.addField(Boolean.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Boolean.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsBoolean()");
                    getterMethods.add(generateGetterMethod(Boolean.class, parameter));
                    setterMethods.add(generateSetterMethod(Boolean.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    structClassBuilder.addField(Integer.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Integer.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsInt()");
                    getterMethods.add(generateGetterMethod(Integer.class, parameter));
                    setterMethods.add(generateSetterMethod(Integer.class, parameter));
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    structClassBuilder.addField(Long.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Long.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsLong()");
                    getterMethods.add(generateGetterMethod(Long.class, parameter));
                    setterMethods.add(generateSetterMethod(Long.class, parameter));
                    break;
                case Consts.DataType.FLOAT:
                    structClassBuilder.addField(Float.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Float.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsFloat()");
                    getterMethods.add(generateGetterMethod(Float.class, parameter));
                    setterMethods.add(generateSetterMethod(Float.class, parameter));
                    break;
                case Consts.DataType.DOUBLE:
                    structClassBuilder.addField(Double.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Double.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsDouble()");
                    getterMethods.add(generateGetterMethod(Double.class, parameter));
                    setterMethods.add(generateSetterMethod(Double.class, parameter));
                    break;
                case Consts.DataType.STRING:
                    structClassBuilder.addField(String.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(String.class, parameter);
                    decodeStatement.add("jsonObject.get(\"" + propIdentifier + "\").getAsString()");
                    getterMethods.add(generateGetterMethod(String.class, parameter));
                    setterMethods.add(generateSetterMethod(String.class, parameter));
                    break;
                default:
                    System.out.println("load thing model file failed, illegal struct:" + identifier);
                    System.exit(0);
            }
            parameterCount++;
            toStringExpression.append("\"");
            if (parameterCount > 1) {
                toStringExpression.append(", ");
            }
            toStringExpression.append(propIdentifier).append("=").append("\"").append(" + ").append(parameter).append(" + ").append("\n");
            encodeMethodBuilder.addStatement("$L.addProperty($S, this.$N)", "jsonObject", propIdentifier, parameter);
            constructorBuilder.addStatement("this.$N = $N", parameter, parameter);
        }

        toStringExpression.append("\"}\"");

        toStringMethodBuilder.addStatement("return $L", toStringExpression.toString());

        int size = decodeStatement.size();
        StringBuilder stringBuilder = new StringBuilder(decodeStatement.get(0));
        if (size > 1) {
            for (int i = 1; i < size - 1; i++) {
                stringBuilder.append(", ");
                stringBuilder.append(decodeStatement.get(i));
            }
            stringBuilder.append(", ").append(decodeStatement.get(size - 1));
        }

        structClassBuilder.addMethod(constructorBuilder.build());

        for (MethodSpec.Builder getterMethod : getterMethods) {
            structClassBuilder.addMethod(getterMethod.build());
        }

        for (MethodSpec.Builder setterMethod : setterMethods) {
            structClassBuilder.addMethod(setterMethod.build());
        }

        classBuilder.addType(structClassBuilder
                .addMethod(encodeMethodBuilder.addStatement("return jsonObject").build())
                .addMethod(decodeMethodBuilder.addStatement("return new $T(" + stringBuilder.toString() + ")", ClassName.get("", nestedClassName)).build())
                .addMethod(toStringMethodBuilder.build())
                .build());
    }

    public static void generateServiceClass(TypeSpec.Builder classBuilder, Service service) {
        // 构建 Service 类
        String identifier = service.getIdentifier();
        String nestedClassName = generateName(identifier);
        TypeSpec.Builder serviceClassBuilder = TypeSpec.classBuilder(nestedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addJavadoc("$L 服务类\n", service.getName());

        List<ServiceData> input = service.getInput();
        generateServiceInputOrOutputClass(serviceClassBuilder, service, input, "Input");

        List<ServiceData> output = service.getOutput();
        generateServiceInputOrOutputClass(serviceClassBuilder, service, output, "Output");

        classBuilder.addType(serviceClassBuilder
                .build());
    }

    public static void generateServiceInputOrOutputClass(TypeSpec.Builder classBuilder, Service service, List<ServiceData> serviceDataList, String inputOrOutput) {
        // 构建 Service 类
        List<MethodSpec.Builder> getterMethods = new ArrayList<>();
        List<MethodSpec.Builder> setterMethods = new ArrayList<>();

        String identifier = service.getIdentifier();
        String lowerPrefixNestedClassName = generateLowPrefixName(identifier);

        inputOrOutput = generateName(inputOrOutput);
        String nestedClassName = generateName(identifier);
        String desc = "Input".equals(inputOrOutput) ? "输入" : "输出";
        String innerClassName = nestedClassName + "." + inputOrOutput;
        TypeSpec.Builder innerClassBuilder = TypeSpec.classBuilder(inputOrOutput)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addJavadoc("$L $L类\n", service.getName(), desc);

        if (!serviceDataList.isEmpty()) {
            MethodSpec.Builder noConstructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
            innerClassBuilder.addMethod(noConstructorBuilder.build());
        }

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder encodeMethodBuilder = MethodSpec.methodBuilder("encode")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T jsonObject = new JsonObject()", JsonObject.class)
                .returns(JsonObject.class);

        MethodSpec.Builder decodeMethodBuilder = MethodSpec.methodBuilder("decode")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(JsonObject.class, "jsonObject")
                .returns(ClassName.get("", innerClassName))
                .addStatement("$L $L = new $L()", innerClassName, lowerPrefixNestedClassName, innerClassName);

        MethodSpec.Builder toStringMethodBuilder = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("java.lang", "Override"))
                .returns(String.class);
        StringBuilder toStringExpression = new StringBuilder("\"{" + innerClassName + ":\" +\n");
        int parameterCount = 0;

        for (ServiceData serviceData : serviceDataList) {
            String propIdentifier = serviceData.getIdentifier();
            String parameter = propIdentifier.replaceAll("-", "");
            String highPrefixParameter = generateName(parameter);
            String type = serviceData.getDataType().getType();
            boolean isArray = false;
            boolean isStruct = false;
            switch (type) {
                case Consts.DataType.BOOL:
                    innerClassBuilder.addField(Boolean.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Boolean.class, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(jsonObject.get($S).getAsBoolean())", lowerPrefixNestedClassName, highPrefixParameter, propIdentifier);
                    getterMethods.add(generateGetterMethod(Boolean.class, parameter));
                    setterMethods.add(generateSetterMethod(Boolean.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    innerClassBuilder.addField(Integer.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Integer.class, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(jsonObject.get($S).getAsInt())", lowerPrefixNestedClassName, highPrefixParameter, propIdentifier);
                    getterMethods.add(generateGetterMethod(Integer.class, parameter));
                    setterMethods.add(generateSetterMethod(Integer.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.DATE:
                case Consts.DataType.INT64:
                    innerClassBuilder.addField(Long.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Long.class, parameter);
                    getterMethods.add(generateGetterMethod(Long.class, parameter));
                    setterMethods.add(generateSetterMethod(Long.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.FLOAT:
                    innerClassBuilder.addField(Float.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Float.class, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(jsonObject.get($S).getAsFloat())", lowerPrefixNestedClassName, highPrefixParameter, propIdentifier);
                    getterMethods.add(generateGetterMethod(Float.class, parameter));
                    setterMethods.add(generateSetterMethod(Float.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.DOUBLE:
                    innerClassBuilder.addField(Double.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(Double.class, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(jsonObject.get($S).getAsDouble())", lowerPrefixNestedClassName, highPrefixParameter, propIdentifier);
                    getterMethods.add(generateGetterMethod(Double.class, parameter));
                    setterMethods.add(generateSetterMethod(Double.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.STRING:
                    innerClassBuilder.addField(String.class, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(String.class, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(jsonObject.get($S).getAsString())", lowerPrefixNestedClassName, highPrefixParameter, propIdentifier);
                    getterMethods.add(generateGetterMethod(String.class, parameter));
                    setterMethods.add(generateSetterMethod(String.class, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.STRUCT:
                    isStruct = true;
                    Property property = new Property();
                    property.setIdentifier(propIdentifier);
                    property.setName(serviceData.getName());
                    property.setDataType(serviceData.getDataType());
                    String structName = generateName(propIdentifier);
                    generateStructClass(innerClassBuilder, property, "");
                    ClassName structClass = ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, innerClassName + "." + structName);
                    innerClassBuilder.addField(structClass, parameter, Modifier.PRIVATE);
                    constructorBuilder.addParameter(structClass, parameter);
                    decodeMethodBuilder.addStatement("$L.set$L(new $T().fromJson(jsonObject.get($S).toString(), $L.class))", lowerPrefixNestedClassName, highPrefixParameter, Gson.class, propIdentifier, structName);
                    getterMethods.add(generateGetterMethod(structClass, parameter));
                    setterMethods.add(generateSetterMethod(structClass, parameter));
                    parameterCount++;
                    break;
                case Consts.DataType.ARRAY:
                    isArray = true;
                    boolean isArrayStruct = false;
                    Object specs = serviceData.getDataType().getSpecs();
                    JsonObject jsonElements = GSON.fromJson(GSON.toJson(specs), JsonObject.class);
                    type = jsonElements.get("type").getAsString();
                    ParameterizedTypeName parameterizedTypeName = null;
                    String genericName = null;
                    TypeName typeName = null;
                    String jsonArrayName = parameter + "JsonArray";
                    if (type.equals(Consts.DataType.STRUCT)) {
                        isArrayStruct = true;
                        property = new Property();
                        property.setIdentifier(propIdentifier);
                        property.setName(serviceData.getName());
                        property.setDataType(serviceData.getDataType());
                        genericName = generateName(propIdentifier);
                        generateStructClass(innerClassBuilder, property, "");
                        typeName = ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, genericName);
                        parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(List.class), typeName);
                    } else {
                        Class innerClass = null;
                        switch (type) {
                            case Consts.DataType.BOOL:
                                innerClass = Boolean.class;
                                genericName = "Boolean";
                                break;
                            case Consts.DataType.INT32:
                            case Consts.DataType.ENUM:
                            case Consts.DataType.BITMAP:
                                innerClass = Integer.class;
                                genericName = "Integer";
                                break;
                            case Consts.DataType.DATE:
                            case Consts.DataType.INT64:
                                innerClass = Long.class;
                                genericName = "Long";
                                break;
                            case Consts.DataType.FLOAT:
                                innerClass = Float.class;
                                genericName = "Float";
                                break;
                            case Consts.DataType.DOUBLE:
                                innerClass = Double.class;
                                genericName = "Double";
                                break;
                            case Consts.DataType.STRING:
                                innerClass = String.class;
                                genericName = "String";
                                break;
                            default:
                                break;
                        }
                        if (innerClass != null) {
                            typeName = TypeVariableName.get(innerClass);
                            parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(List.class), typeName);
                        } else {
                            System.out.println("load thing model file failed, illegal dataType:" + type);
                            System.exit(0);
                        }
                    }
                    CodeBlock.Builder builder = CodeBlock.builder()
                            .beginControlFlow("for ($L obj : $L)", typeName, parameter);
                    if (isArrayStruct) {
                        builder.addStatement("$L.add(obj.encode())", jsonArrayName);
                    } else {
                        builder.addStatement("$L.add(obj)", jsonArrayName);
                    }
                    encodeMethodBuilder.addStatement("$T $L = new JsonArray()", JsonArray.class, jsonArrayName)
                            .addCode(builder.endControlFlow().build())
                            .addStatement("$L.add($S, $L)", "jsonObject", propIdentifier, jsonArrayName);
                    decodeMethodBuilder.addStatement("$L.set$L(new $T().fromJson(jsonObject.get($S).toString(), new $T<List<$L>>(){}.getType()))", lowerPrefixNestedClassName, highPrefixParameter, Gson.class, propIdentifier, TypeToken.class, genericName);
                    constructorBuilder.addParameter(parameterizedTypeName, parameter);
                    innerClassBuilder.addField(parameterizedTypeName, parameter, Modifier.PRIVATE);
                    getterMethods.add(generateGetterMethod(parameterizedTypeName, parameter));
                    setterMethods.add(generateSetterMethod(parameterizedTypeName, parameter));
                    parameterCount++;
                    break;
                default:
                    System.out.println("load thing model file failed, illegal struct:" + identifier);
                    System.exit(0);
            }
            toStringExpression.append("\"");
            if (parameterCount > 1) {
                toStringExpression.append(", ");
            }
            toStringExpression.append(propIdentifier).append("=").append("\"").append(" + ").append(parameter).append(" + ").append("\n");
            if (isStruct) {
                encodeMethodBuilder.addStatement("$L.add($S, this.$N.encode())", "jsonObject", propIdentifier, parameter);
            } else if (!isArray) {
                encodeMethodBuilder.addStatement("$L.addProperty($S, this.$N)", "jsonObject", propIdentifier, parameter);
            }
            constructorBuilder.addStatement("this.$N = $N", parameter, parameter);
        }
        toStringExpression.append("\"}\"");

        toStringMethodBuilder.addStatement("return $L", toStringExpression.toString());

        innerClassBuilder.addMethod(constructorBuilder.build());

        for (MethodSpec.Builder getterMethod : getterMethods) {
            innerClassBuilder.addMethod(getterMethod.build());
        }

        for (MethodSpec.Builder setterMethod : setterMethods) {
            innerClassBuilder.addMethod(setterMethod.build());
        }

        classBuilder.addType(innerClassBuilder
                .addMethod(encodeMethodBuilder.addStatement("return jsonObject").build())
                .addMethod(decodeMethodBuilder.addStatement("return $L", lowerPrefixNestedClassName).build())
                .addMethod(toStringMethodBuilder.build())
                .build());
    }

    private static MethodSpec.Builder generateGetterMethod(Type type, String parameter) {
        String methodName;
        if (type == Boolean.class) {
            methodName = parameter;
        } else {
            methodName = "get" + generateName(parameter);
        }
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$L", parameter)
                .returns(type);
    }

    private static MethodSpec.Builder generateGetterMethod(TypeName typename, String parameter) {
        String methodName;
        if (typename.equals(TypeName.BOOLEAN)) {
            methodName = parameter;
        } else {
            methodName = "get" + generateName(parameter);
        }
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$L", parameter)
                .returns(typename);
    }

    private static MethodSpec.Builder generateSetterMethod(Type type, String parameter) {
        String methodName = "set" + generateName(parameter);
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, parameter)
                .addStatement("this.$L = $L", parameter, parameter);
    }

    private static MethodSpec.Builder generateSetterMethod(TypeName typename, String parameter) {
        String methodName = "set" + generateName(parameter);
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typename, parameter)
                .addStatement("this.$L = $L", parameter, parameter);
    }

}
