package com.github.cm.heclouds.adapter.thing.processor;

import com.github.cm.heclouds.adapter.thing.schema.Event;
import com.github.cm.heclouds.adapter.thing.schema.Property;
import com.github.cm.heclouds.adapter.thing.schema.Schema;
import com.github.cm.heclouds.adapter.thing.schema.services.Service;
import com.github.cm.heclouds.adapter.thing.util.CommonUtil;
import com.github.cm.heclouds.adapter.thing.util.MethodUtil;
import com.github.cm.heclouds.adapter.thing.util.ModelUtil;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.github.cm.heclouds.adapter.thing.util.CommonUtil.GSON;

/**
 * 处理产品物模型
 */
final class ChildDevProcessor {

    static void doProcessProductConfiguration(String configPath, ProcessingEnvironment processingEnv) throws Exception {

        try {
            configPath = ChildDevProcessor.class.getClassLoader().getResource(configPath).getPath();
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

        String propertyApiExtensionClassName = "Property";
        String eventApiExtensionClassName = "Event";
        String serviceExtensionClassName = "Service";

        // 构建 物模型 类
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(configFileNameSuffix)
                .addJavadoc("编译器自动生成，请勿修改\n")
                .addJavadoc("\n")
                .addJavadoc("该类根据配置的功能点自动生成对应代码，开发者根据这些方法实现相应功能即可\n")
                .addModifiers(Modifier.PUBLIC);

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

        List<Property> properties = schema.getProperties();
        List<Event> events = schema.getEvents();
        List<Service> services = schema.getServices();

        List<MethodSpec> propertyGenerateMethods = new ArrayList<>();
        List<MethodSpec> eventGenerateMethods = new ArrayList<>();
        List<MethodSpec> serviceGenerateMethods = new ArrayList<>();

        for (Property property : properties) {
            propertyGenerateMethods.add(MethodUtil.generatePropertyJsonObjectGenerateMethods(propertyClassBuilder, property));
            String identifier = property.getIdentifier();
            String name = property.getName();
            propertiesConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        for (Event event : events) {
            eventGenerateMethods.add(MethodUtil.generateEventJsonObjectMethod(eventClassBuilder, event));
            String identifier = event.getIdentifier();
            String name = event.getName();
            eventsConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        for (Service service : services) {
            ModelUtil.generateServiceClass(serviceClassBuilder, service);
            serviceGenerateMethods.add(MethodUtil.generateServicesOutputGenerateMethods(service));
            String identifier = service.getIdentifier();
            String name = service.getName();
            servicesConstsClassBuilder.addField(CommonUtil.generatePublicStaticFinalField(identifier.toUpperCase(), identifier, name));
        }

        propertyClassBuilder.addType(propertiesConstsClassBuilder.build());
        eventClassBuilder.addType(eventsConstsClassBuilder.build());
        serviceClassBuilder.addType(servicesConstsClassBuilder.build());

        for (MethodSpec method : propertyGenerateMethods) {
            propertyClassBuilder.addMethod(method);
        }

        for (MethodSpec method : eventGenerateMethods) {
            eventClassBuilder.addMethod(method);
        }

        for (MethodSpec method : serviceGenerateMethods) {
            serviceClassBuilder.addMethod(method);
        }

        TypeSpec.Builder builder = classBuilder
                .addType(propertyClassBuilder.build())
                .addType(eventClassBuilder.build())
                .addType(serviceClassBuilder.build());

        // 写入文件
        JavaFile classFile = JavaFile.builder(CommonUtil.API_EXTENSION_PACKAGE, builder.build()).indent("    ").build();
        classFile.writeTo(processingEnv.getFiler());
    }
}
