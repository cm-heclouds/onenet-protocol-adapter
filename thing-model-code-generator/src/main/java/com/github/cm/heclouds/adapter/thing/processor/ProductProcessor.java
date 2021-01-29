package com.github.cm.heclouds.adapter.thing.processor;

import com.github.cm.heclouds.adapter.thing.schema.Consts;
import com.github.cm.heclouds.adapter.thing.schema.Event;
import com.github.cm.heclouds.adapter.thing.schema.Property;
import com.github.cm.heclouds.adapter.thing.schema.Schema;
import com.github.cm.heclouds.adapter.thing.schema.services.Service;
import com.github.cm.heclouds.adapter.thing.util.CommonUtil;
import com.github.cm.heclouds.adapter.thing.util.MethodUtil;
import com.github.cm.heclouds.adapter.thing.util.ModelUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.cm.heclouds.adapter.thing.util.CommonUtil.*;

/**
 * 处理产品物模型
 */
final class ProductProcessor {

    static void doProcessProductConfiguration(String configPath, ProcessingEnvironment processingEnv) throws Exception {

        try {
            configPath = ProductProcessor.class.getClassLoader().getResource(configPath).getPath();
        } catch (Exception e) {
            System.out.println("load thing model file failed, file not exist");
            return;
        }
        // 转换配置文件
        String config = CommonUtil.readFileToString(configPath);
        Schema schema = null;
        try {
            schema = GSON.fromJson(config, Schema.class);
        } catch (Exception e) {
            System.out.println("load thing model file failed, illegal model file");
            System.exit(1);
        }

        if (schema == null) {
            System.out.println("load thing model file failed, empty model file");
            System.exit(0);
        }

        String configFileNameSuffix = CommonUtil.getConfigFileSuffix(configPath);

        String adapterApiExtensionClassName = "AdapterApiExtension";
        String propertyApiExtensionClassName = "Property";
        String eventApiExtensionClassName = "Event";
        String serviceExtensionClassName = "Service";

        // 构建 物模型 类
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(configFileNameSuffix)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("该类根据配置的功能点自动生成对应代码，开发者根据这些方法实现相应功能即可\n")
                .addModifiers(Modifier.PUBLIC);

        // 构建 AdapterApiExtension 类
        TypeSpec.Builder adapterApiExtensionClassBuilder = TypeSpec.classBuilder(adapterApiExtensionClassName)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("{@link AdapterApi} 扩展类，该类根据配置的功能点自动生成对应的上报方法，开发者根据这些方法实现相应功能即可\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);

        // 构建 Property 类
        TypeSpec.Builder propertyClassBuilder = TypeSpec.classBuilder(propertyApiExtensionClassName)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("该类根据配置的功能点自动生成Property相关类和方法\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);

        // 构建 Event 类
        TypeSpec.Builder eventClassBuilder = TypeSpec.classBuilder(eventApiExtensionClassName)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("该类根据配置的功能点自动生成Event相关类和方法\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);

        // 构建 Service 类
        TypeSpec.Builder serviceClassBuilder = TypeSpec.classBuilder(serviceExtensionClassName)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("该类根据配置的功能点自动生成Service相关类和方法\n")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);

        // AdapterApiExtension类构造方法
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CLASS_ADAPTER_API, "adapterApi")
                .addStatement("this.$N = $N", "adapterApi", "adapterApi")
                .build();

        TypeSpec.Builder propertiesConstsClassBuilder = TypeSpec.classBuilder("Identifier")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("属性唯一标识符常量\n");

        TypeSpec.Builder eventsConstsClassBuilder = TypeSpec.classBuilder("Identifier")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("事件唯一标识符常量\n");

        TypeSpec.Builder servicesConstsClassBuilder = TypeSpec.classBuilder("Identifier")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.FINAL)
                .addJavadoc("服务唯一标识符常量\n");

        adapterApiExtensionClassBuilder.addField(CLASS_ADAPTER_API, "adapterApi", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor);

        // 物模型属性设置下发请求接收器
        TypeSpec.Builder propertySetListenerClassBuilder = TypeSpec.classBuilder("DevicePropertySetListenerExtension")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                .addSuperinterface(CLASS_PROPERTY_SET_REQUEST_LISTENER)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("物模型属性设置下发请求接收器，实现该类的抽象方法即可。\n");

        // 物模型服务调用请求接收器
        TypeSpec.Builder serviceInvokeListenerClassBuilder = TypeSpec.classBuilder("DeviceServiceInvokeListenerExtension")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addSuperinterface(CLASS_SERVICE_INVOKE_REQUEST_LISTENER)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("物模型服务调用请求接收器，实现该类的抽象方法即可。\n");

        // onPropertySetRequestReceivedBuilder 方法
        MethodSpec.Builder onPropertySetRequestReceivedBuilder = MethodSpec.methodBuilder("onRequestReceived")
                .addJavadoc("{@link com.github.cm.heclouds.adapter.api.DevicePropertySetListener#onRequestReceived(Device, String, String, JsonObject)}的实现\n")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version")
                .addParameter(JsonObject.class, "params");

        // onServiceInvokeRequestReceivedBuilder 方法
        MethodSpec.Builder onServiceInvokeRequestReceivedBuilder = MethodSpec.methodBuilder("onRequestReceived")
                .addJavadoc("{@link com.github.cm.heclouds.adapter.api.DeviceServiceInvokeListener#onRequestReceived(Device, String, String, String, JsonObject)}的实现\n")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CLASS_DEVICE, "device")
                .addParameter(String.class, "identifier")
                .addParameter(String.class, "id")
                .addParameter(String.class, "version")
                .addParameter(JsonObject.class, "params");

        CodeBlock.Builder propertySetListenerForEachBlock = CodeBlock.builder()
                .beginControlFlow("for ($T<$T, $T> entry : params.entrySet())", Map.Entry.class, String.class, JsonElement.class)
                .addStatement("$T key = entry.getKey()", String.class)
                .addStatement("$T value = entry.getValue()", JsonElement.class)
                .beginControlFlow("switch (key)");

        CodeBlock.Builder serviceInvokeListenerForEachBlock = CodeBlock.builder()
                .beginControlFlow("switch (identifier)");

        List<Property> properties = schema.getProperties();
        List<Event> events = schema.getEvents();
        List<Service> services = schema.getServices();

        List<MethodSpec> propertyGenerateMethods = new ArrayList<>();
        List<MethodSpec> eventGenerateMethods = new ArrayList<>();
        List<MethodSpec> serviceGenerateMethods = new ArrayList<>();
        List<MethodSpec> uploadMethods = new ArrayList<>();
        List<MethodSpec> propertySetRequestReceiveMethods = new ArrayList<>();
        List<MethodSpec> serviceInvokeRequestReceiveMethods = new ArrayList<>();

        for (Property property : properties) {
            String accessMode = property.getAccessMode();
            if (Consts.AccessMode.READ_AND_WRITE.equals(accessMode)) {
                propertySetRequestReceiveMethods.add(MethodUtil.generatePropertySetOnRequestMethod(propertySetListenerForEachBlock, property, configFileNameSuffix));
            }
            propertyGenerateMethods.add(MethodUtil.generatePropertyGenerateMethods(propertyClassBuilder, property));
            uploadMethods.add(MethodUtil.generatePropertyUploadMethod(property));
            String identifier = property.getIdentifier();
            String name = property.getName();
            propertiesConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        for (Event event : events) {
            eventGenerateMethods.add(MethodUtil.generateEventGenerateMethod(eventClassBuilder, event));
            uploadMethods.add(MethodUtil.generateEventUploadMethod(event));
            String identifier = event.getIdentifier();
            String name = event.getName();
            eventsConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        for (Service service : services) {
            ModelUtil.generateServiceClass(serviceClassBuilder, service);
            serviceInvokeRequestReceiveMethods.add(MethodUtil.generateServiceInvokeOnRequestMethod(serviceInvokeListenerForEachBlock, service, configFileNameSuffix));
            serviceGenerateMethods.add(MethodUtil.generateServicesOutputGenerateMethods(service));
            uploadMethods.add(MethodUtil.generateServiceReplyMethod(service));
            uploadMethods.add(MethodUtil.generateServiceReplyMethodWithClass(service));
            String identifier = service.getIdentifier();
            String name = service.getName();
            servicesConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        propertyClassBuilder.addType(propertiesConstsClassBuilder.build());
        eventClassBuilder.addType(eventsConstsClassBuilder.build());
        serviceClassBuilder.addType(servicesConstsClassBuilder.build());

        propertySetListenerForEachBlock.add("default:\n")
                .indent()
                .addStatement("break")
                .unindent()
                .endControlFlow()
                .endControlFlow();

        serviceInvokeListenerForEachBlock.add("default:\n")
                .indent()
                .addStatement("break")
                .unindent()
                .endControlFlow();

        onPropertySetRequestReceivedBuilder.addCode(propertySetListenerForEachBlock.build());
        propertySetListenerClassBuilder.addMethod(onPropertySetRequestReceivedBuilder.build());

        onServiceInvokeRequestReceivedBuilder.addCode(serviceInvokeListenerForEachBlock.build());
        serviceInvokeListenerClassBuilder.addMethod(onServiceInvokeRequestReceivedBuilder.build());

        for (MethodSpec method : propertyGenerateMethods) {
            propertyClassBuilder.addMethod(method);
        }

        for (MethodSpec method : eventGenerateMethods) {
            eventClassBuilder.addMethod(method);
        }

        for (MethodSpec method : serviceGenerateMethods) {
            serviceClassBuilder.addMethod(method);
        }

        for (MethodSpec method : uploadMethods) {
            adapterApiExtensionClassBuilder.addMethod(method);
        }

        for (MethodSpec methodSpec : propertySetRequestReceiveMethods) {
            propertySetListenerClassBuilder.addMethod(methodSpec);
        }

        for (MethodSpec methodSpec : serviceInvokeRequestReceiveMethods) {
            serviceInvokeListenerClassBuilder.addMethod(methodSpec);
        }

        propertyClassBuilder.addType(propertySetListenerClassBuilder.build());
        serviceClassBuilder.addType(serviceInvokeListenerClassBuilder.build());

        TypeSpec.Builder builder = classBuilder
                .addType(adapterApiExtensionClassBuilder.build())
                .addType(propertyClassBuilder.build())
                .addType(eventClassBuilder.build())
                .addType(serviceClassBuilder.build());

        // 写入文件
        JavaFile classFile = JavaFile.builder(CommonUtil.API_EXTENSION_PACKAGE, builder.build()).indent("    ").build();
        classFile.writeTo(processingEnv.getFiler());
    }
}
