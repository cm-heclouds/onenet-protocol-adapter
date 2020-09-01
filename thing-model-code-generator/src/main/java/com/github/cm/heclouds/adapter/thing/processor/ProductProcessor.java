package com.github.cm.heclouds.adapter.thing.processor;

import com.github.cm.heclouds.adapter.core.entity.OneJSONRequest;
import com.github.cm.heclouds.adapter.thing.schema.*;
import com.github.cm.heclouds.adapter.thing.util.ProcessorUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理产品物模型
 */
class ProductProcessor {

    private static final ClassName CLASS_OPEN_API = ClassName.get(ProcessorUtil.API_EXTENSION_PACKAGE, "OpenApi");
    private static final ClassName CLASS_DEVICE = ClassName.get("com.github.cm.heclouds.adapter.core.entity", "Device");
    private static final ClassName CLASS_THING_COMMAND_LISTENER = ClassName.get("com.github.cm.heclouds.adapter.api", "DeviceCommandListener");

    static void doProcessProductConfiguration(String configPath, ProcessingEnvironment processingEnv) throws Exception {

        try {
            configPath = ProductProcessor.class.getClassLoader().getResource(configPath).getPath();
        } catch (Exception e) {
            System.out.println("load thing model file failed, file not exist");
            System.exit(0);
        }
        // 转换配置文件
        String config = ProcessorUtil.readFileToString(configPath);
        Schema schema = null;
        try {
            schema = ProcessorUtil.GSON.fromJson(config, Schema.class);
        } catch (Exception e) {
            System.out.println("load thing model file failed, illegal model file");
            System.exit(0);
        }

        if (schema == null) {
            System.out.println("load thing model file failed, empty model file");
            System.exit(0);
        }

        String configFileNameSuffix = ProcessorUtil.getConfigFileSuffix(configPath);

        String openApiExtensionClassName = "OpenApiExtension" + configFileNameSuffix;

        // 构建 OpenApiExtension 类
        TypeSpec.Builder openApiExtensionClassBuilder = TypeSpec.classBuilder(openApiExtensionClassName)
                .addJavadoc("编译器自动生成，请勿修改<p/>\n")
                .addJavadoc("\n")
                .addJavadoc("{@link OpenApi} 扩展类，该类根据配置的功能点自动生成对应的上报方法，开发者根据这些方法实现相应功能即可\n")
                .addModifiers(Modifier.PUBLIC);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CLASS_OPEN_API, "openApi")
                .addStatement("this.$N = $N", "openApi", "openApi")
                .build();

        TypeSpec.Builder propertiesConstsClassBuilder = TypeSpec.classBuilder("PropertyIdentifier")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("属性唯一标识符常量\n");

        TypeSpec.Builder eventsConstsClassBuilder = TypeSpec.classBuilder("EventIdentifier")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("事件唯一标识符常量\n");

        openApiExtensionClassBuilder.addField(CLASS_OPEN_API, "openApi", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor);

        TypeSpec.Builder commandListenerClassBuilder = TypeSpec.classBuilder("DeviceCommandListenerExtension" + configFileNameSuffix)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addSuperinterface(CLASS_THING_COMMAND_LISTENER)
                .addJavadoc("编译器自动生成，请勿修改<p/>\n")
                .addJavadoc("\n")
                .addJavadoc("命令响应接收器，实现该类的抽象方法即可。\n");

        // onCommandReceived 方法
        MethodSpec.Builder onCommandReceivedBuilder = MethodSpec.methodBuilder("onCommandReceived")
                .addJavadoc("{@link com.cmiot.adapter.api.DeviceCommandListener#onCommandReceived(Device, String, String, JsonObject)}的实现\n")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version")
                .addParameter(JsonObject.class, "params");

        CodeBlock.Builder forEachBlock = CodeBlock.builder()
                .beginControlFlow("for ($T<$T, $T> entry : params.entrySet())", Map.Entry.class, String.class, JsonElement.class)
                .addStatement("$T key = entry.getKey()", String.class)
                .addStatement("$T value = entry.getValue()", JsonElement.class)
                .beginControlFlow("switch (key)");

        List<Property> properties = schema.getProperties();
        List<Event> events = schema.getEvents();

        List<MethodSpec> generateMethods = new ArrayList<>();
        List<MethodSpec> uploadMethods = new ArrayList<>();
        List<MethodSpec> onReceiveMethods = new ArrayList<>();

        for (Property property : properties) {
            String accessMode = property.getAccessMode();
            if (Consts.AccessMode.READ_AND_WRITE.equals(accessMode)) {
                onReceiveMethods.add(generateOnReceiveMethod(forEachBlock, property, openApiExtensionClassName));
            }
            generateMethods.add(generatePropertyGenerateMethods(openApiExtensionClassBuilder, property));
            uploadMethods.add(generatePropertyUploadMethod(property));
            String identifier = property.getIdentifier();
            String name = property.getName();
            propertiesConstsClassBuilder.addField(ProcessorUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        for (Event event : events) {
            generateMethods.add(generateEventGenerateMethod(openApiExtensionClassBuilder, event));
            uploadMethods.add(generateEventUploadMethod(event));
            String identifier = event.getIdentifier();
            String name = event.getName();
            eventsConstsClassBuilder.addField(ProcessorUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        openApiExtensionClassBuilder.addType(propertiesConstsClassBuilder.build())
                .addType(eventsConstsClassBuilder.build());

        forEachBlock.add("default:\n")
                .indent()
                .addStatement("break")
                .unindent()
                .endControlFlow()
                .endControlFlow();

        onCommandReceivedBuilder.addCode(forEachBlock.build());
        commandListenerClassBuilder.addMethod(onCommandReceivedBuilder.build());

        for (MethodSpec method : generateMethods) {
            openApiExtensionClassBuilder.addMethod(method);
        }

        for (MethodSpec method : uploadMethods) {
            openApiExtensionClassBuilder.addMethod(method);
        }

        for (MethodSpec methodSpec : onReceiveMethods) {
            commandListenerClassBuilder.addMethod(methodSpec);
        }

        TypeSpec openApiExtensionClass = openApiExtensionClassBuilder.build();
        TypeSpec commandListenerExtensionClass = commandListenerClassBuilder.build();
        // 写入文件
        JavaFile apiExtensionFile = JavaFile.builder(ProcessorUtil.API_EXTENSION_PACKAGE, openApiExtensionClass).indent("    ").build();
        JavaFile commandListenerExtensionFile = JavaFile.builder(ProcessorUtil.API_EXTENSION_PACKAGE, commandListenerExtensionClass).indent("    ").build();
        apiExtensionFile.writeTo(processingEnv.getFiler());
        commandListenerExtensionFile.writeTo(processingEnv.getFiler());
    }

    /**
     * 构建属性上传方法
     */
    private static MethodSpec generatePropertyUploadMethod(Property property) {
        String identifier = property.getIdentifier();
        String functionName = ProcessorUtil.generateUploadMethodName(identifier, "Property");
        StringBuilder generateFunction = new StringBuilder(ProcessorUtil.generateGenerateMethodName(identifier, "Property"));
        generateFunction.append("(id, version, ");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("上传 " + property.getName() + " 属性\n")
                .addJavadoc("desc: " + property.getDesc() + "\n")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC);
        builder.addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version");

        DataType propertyDataType = property.getDataType();
        String propertyIdentifier = property.getIdentifier();
        switch (propertyDataType.getType()) {
            case Consts.DataType.BOOL:
                builder.addParameter(Boolean.class, "value");
                break;
            case Consts.DataType.INT32:
            case Consts.DataType.ENUM:
            case Consts.DataType.BITMAP:
                builder.addParameter(Integer.class, "value");
                break;
            case Consts.DataType.INT64:
                builder.addParameter(Long.class, "value");
                break;
            case Consts.DataType.FLOAT:
                builder.addParameter(Float.class, "value");
                break;
            case Consts.DataType.DOUBLE:
                builder.addParameter(Double.class, "value");
                break;
            case Consts.DataType.STRING:
                builder.addParameter(String.class, "value")
                ;
                break;
            case Consts.DataType.STRUCT:
                propertyIdentifier = property.getIdentifier();
                builder.addParameter(ClassName.get("", ProcessorUtil.generateName(propertyIdentifier) + "Property"), "value");
                break;
            case Consts.DataType.ARRAY:
                String className = ProcessorUtil.generateName(propertyIdentifier) + "Property";
                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(className)), "value");
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        builder.addParameter(Long.class, "time");
        generateFunction.append("value, time)");
        builder.addStatement("return openApi.uploadProperty(device, $L)", generateFunction.toString());
        return builder.build();
    }

    /**
     * 构建属性生成方法
     */
    private static MethodSpec generatePropertyGenerateMethods(TypeSpec.Builder classBuilder, Property property) {
        String identifier = property.getIdentifier();
        String identifierReplace = ProcessorUtil.generateLowPrefixName(identifier) + "JsonObject";
        String functionName = ProcessorUtil.generateGenerateMethodName(identifier, "Property");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + property.getName() + " 属性\n")
                .addJavadoc("desc: " + property.getDesc() + "\n")
                .returns(OneJSONRequest.class)
                .addModifiers(Modifier.PUBLIC);
        builder.addParameter(String.class, "id")
                .addParameter(String.class, "version");

        CodeBlock.Builder ifIdIsNull = CodeBlock.builder()
                .beginControlFlow("if (id == null)")
                .addStatement("id = $T.valueOf($T.currentTimeMillis())", String.class, System.class)
                .endControlFlow();

        builder.addCode(ifIdIsNull.build());

        ProcessorUtil.generateProperties(classBuilder, builder, property, identifierReplace);

        builder.addStatement("return new $T(id, version, param)", OneJSONRequest.class);
        return builder.build();
    }

    /**
     * 构建事件上报方法
     */
    private static MethodSpec generateEventUploadMethod(Event event) {
        List<Property> outputData = event.getOutputData();
        String identifier = event.getIdentifier();
        String functionName = ProcessorUtil.generateUploadMethodName(identifier, "Event");
        StringBuilder generateFunction = new StringBuilder(ProcessorUtil.generateGenerateMethodName(identifier, "Event"));
        generateFunction.append("(id, version, ");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("上报 " + event.getName() + " 事件\n")
                .addJavadoc("desc: " + event.getDesc() + "\n")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC);
        builder.addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version");

        for (Property property : outputData) {
            DataType propertyDataType = property.getDataType();
            String propertyIdentifier = property.getIdentifier();
            String valueName = ProcessorUtil.generateLowPrefixName(propertyIdentifier) + "Value";
            switch (propertyDataType.getType()) {
                case Consts.DataType.BOOL:
                    builder.addParameter(Boolean.class, valueName);
                    break;
                case Consts.DataType.INT32:
                case Consts.DataType.ENUM:
                case Consts.DataType.BITMAP:
                    builder.addParameter(Integer.class, valueName);
                    break;
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
                    String parameterValue = ProcessorUtil.generateLowPrefixName(propertyIdentifier);
                    builder.addParameter(ClassName.get("", ProcessorUtil.generateName(propertyIdentifier) + "Event"), parameterValue);
                    valueName = parameterValue;
                    break;
                case Consts.DataType.ARRAY:
                    parameterValue = ProcessorUtil.generateLowPrefixName(propertyIdentifier) + "List";
                    String className = ProcessorUtil.generateName(propertyIdentifier) + "Event";
                    builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(className)), parameterValue);
                    valueName = parameterValue;
                    break;
                default:
                    System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                    System.exit(0);
            }
            generateFunction.append(valueName)
                    .append(", ");
        }
        builder.addParameter(Long.class, "time");
        generateFunction.append("time)");
        builder.addStatement("return openApi.uploadEvent(device, $L)", generateFunction.toString());
        return builder.build();
    }

    /**
     * 构建事件生成方法
     */
    private static MethodSpec generateEventGenerateMethod(TypeSpec.Builder classBuilder, Event event) {
        String identifier = event.getIdentifier();
        String identifierReplace = ProcessorUtil.generateLowPrefixName(identifier) + "JsonObject";
        String functionName = ProcessorUtil.generateGenerateMethodName(identifier, "Event");
        MethodSpec.Builder builder = MethodSpec.methodBuilder(functionName)
                .addJavadoc("生成 " + event.getName() + " 事件\n")
                .addJavadoc("desc: " + event.getDesc() + "\n")
                .returns(OneJSONRequest.class)
                .addModifiers(Modifier.PUBLIC);
        builder.addParameter(String.class, "id")
                .addParameter(String.class, "version");

        CodeBlock.Builder ifIdIsNull = CodeBlock.builder()
                .beginControlFlow("if (id == null)")
                .addStatement("id = $T.valueOf($T.currentTimeMillis())", String.class, System.class)
                .endControlFlow();

        builder.addCode(ifIdIsNull.build());

        ProcessorUtil.generateEvents(classBuilder, builder, event, identifierReplace);

        builder.addStatement("return new $T(id, version, param)", OneJSONRequest.class);
        return builder.build();
    }

    /**
     * 构建消息接收方法
     */
    private static MethodSpec generateOnReceiveMethod(CodeBlock.Builder forEachBlock, Property property, String openApiExtensionClassName) {
        String identifier = property.getIdentifier();
        String methodName = ProcessorUtil.generateOnReceiveMethodName(identifier);
        forEachBlock.add("case $S:\n", identifier)
                .indent();

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
                ClassName structClass = ClassName.get(ProcessorUtil.API_EXTENSION_PACKAGE, openApiExtensionClassName + "." + ProcessorUtil.generateName(identifier) + "Property");
                builder.addParameter(structClass, "value");
                forEachBlock.addStatement(methodName + "(device, id, version, $T.decode(value.getAsJsonObject()))", structClass);
                break;
            case Consts.DataType.ARRAY:
                String className = openApiExtensionClassName + "." + ProcessorUtil.generateName(identifier) + "Property";
                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(className)), "value");
                forEachBlock.addStatement("$T array = value.getAsJsonArray()", JsonArray.class)
                        .addStatement("$T<$T> list = new $T()", List.class, ClassName.get(ProcessorUtil.API_EXTENSION_PACKAGE, openApiExtensionClassName + "." + ProcessorUtil.generateName(identifier) + "Property"), ArrayList.class)
                        .beginControlFlow("for ($T jsonElement : array)", JsonElement.class)
                        .addStatement("list.add($T.decode(jsonElement.getAsJsonObject()))", ClassName.get(ProcessorUtil.API_EXTENSION_PACKAGE, openApiExtensionClassName + "." + ProcessorUtil.generateName(identifier) + "Property"))
                        .endControlFlow()
                        .addStatement(methodName + "(device, id, version, list)");
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        forEachBlock.addStatement("break")
                .unindent();
        return builder.build();
    }
}
