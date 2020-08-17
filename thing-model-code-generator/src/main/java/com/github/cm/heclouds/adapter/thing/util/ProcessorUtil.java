package com.github.cm.heclouds.adapter.thing.util;

import com.github.cm.heclouds.adapter.thing.schema.Consts;
import com.github.cm.heclouds.adapter.thing.schema.DataType;
import com.github.cm.heclouds.adapter.thing.schema.Event;
import com.github.cm.heclouds.adapter.thing.schema.Property;
import com.google.gson.*;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 工具类
 */
public class ProcessorUtil {

    public static final String API_EXTENSION_PACKAGE = "com.github.cm.heclouds.api";

    public static final Gson GSON = new Gson();

    private ProcessorUtil() {
    }

    public static void generateProperties(TypeSpec.Builder classBuilder, MethodSpec.Builder builder, Property property, String identifierReplace) {
        DataType propertyDataType = property.getDataType();
        String propertyIdentifier = property.getIdentifier();
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
                propertyIdentifier = property.getIdentifier();
                String structName = generateName(propertyIdentifier) + "Property";
                generateStructClass(classBuilder, property, "Property");
                builder.addParameter(ClassName.get("", structName), "struct")
                        .addStatement("$T value = $L.encode()", JsonObject.class, "struct")
                        .addStatement("$L.add(\"value\", value)", identifierReplace);
                break;
            case Consts.DataType.ARRAY:
                propertyIdentifier = property.getIdentifier();
                structName = generateName(propertyIdentifier) + "Property";
                generateStructClass(classBuilder, property, "Property");
                builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(structName)), "list")
                        .addStatement("$T value = new JsonArray()", JsonArray.class)
                        .addCode(CodeBlock.builder()
                                .beginControlFlow("for ($L obj : list)", structName)
                                .addStatement("value.add(obj.encode())")
                                .endControlFlow().build())
                        .addStatement("$L.add(\"value\", value)", identifierReplace);
                break;
            default:
                System.out.println("load thing model file failed, illegal dataType.type:" + propertyDataType.getType());
                System.exit(0);
        }
        builder.addParameter(Long.class, "time")
                .addStatement("$L.addProperty(\"time\", time)", identifierReplace);
    }


    public static void generateEvents(TypeSpec.Builder classBuilder, MethodSpec.Builder builder, Event event, String identifierReplace) {
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
                    String structName = generateName(propertyIdentifier) + "Event";
                    String structValueName = generateLowPrefixName(propertyIdentifier) + "Event";
                    String structValueJsonObjectName = structValueName + "Value";
                    generateStructClass(classBuilder, property, "Event");
                    builder.addParameter(ClassName.get("", structName), structValueName)
                            .addStatement("$T $L = $L.encode()", JsonObject.class, structValueJsonObjectName, structValueName);
                    valueName = structValueJsonObjectName;
                    builder.addStatement("value.add($S, $L)", propertyIdentifier, valueName);
                    break;
                case Consts.DataType.ARRAY:
                    structName = generateName(propertyIdentifier) + "Event";
                    structValueName = generateLowPrefixName(propertyIdentifier) + "EventList";
                    String structValueJsonArrayName = structValueName + "Value";
                    generateStructClass(classBuilder, property, "Event");
                    builder.addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(structName)), structValueName)
                            .addStatement("$T $L = new JsonArray()", JsonObject.class, structValueJsonArrayName)
                            .addCode(CodeBlock.builder()
                                    .beginControlFlow("for ($L obj : list)", structName)
                                    .addStatement("value.add(obj.encode())")
                                    .endControlFlow().build());
                    valueName = structValueJsonArrayName;
                    builder.addStatement("value.add($S, $L)", propertyIdentifier, valueName);
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
            specArray = jsonElements.getAsJsonObject("items").getAsJsonArray("specs");
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

    public static String readFileToString(String path) {
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis, UTF_8);
            br = new BufferedReader(isr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine);
            }
        } catch (IOException e) {
            System.out.println("load thing model config file failed, file not exist");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentBuilder.toString();
    }

    public static String generateName(String text) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            String result = word.replaceFirst(firstLetter, firstLetter.toUpperCase());
            sb.append(result);
        }
        return sb.toString();
    }

    public static String generateLowPrefixName(String text) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder();
        String firstLetter = words[0].substring(0, 1);
        String result = words[0].replaceFirst(firstLetter, firstLetter.toLowerCase());
        sb.append(result);
        if (words.length > 1) {
            for (int i = 1; i < words.length; i++) {
                firstLetter = words[i].substring(0, 1);
                result = words[i].replaceFirst(firstLetter, firstLetter.toUpperCase());
                sb.append(result);
            }
        }
        return sb.toString();
    }


    public static String generateGenerateMethodName(String text, String suffix) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder("generate");
        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            String result = word.replaceFirst(firstLetter, firstLetter.toUpperCase());
            sb.append(result);
        }
        sb.append(suffix);
        return sb.toString();
    }

    public static String generateUploadMethodName(String text, String suffix) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder("upload");
        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            String result = word.replaceFirst(firstLetter, firstLetter.toUpperCase());
            sb.append(result);
        }
        sb.append(suffix);
        return sb.toString();
    }

    public static String generateOnReceiveMethodName(String text) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder("onReceive");
        for (String word : words) {
            String firstLetter = word.substring(0, 1);
            String result = word.replaceFirst(firstLetter, firstLetter.toUpperCase());
            sb.append(result);
        }
        return sb.toString();
    }

    public static FieldSpec generatePublicStaticFinalField(String fieldName, String fieldValue, String comment) {
        FieldSpec fieldSpec = null;
        if (fieldValue != null) {
            fieldSpec = FieldSpec.builder(String.class, fieldName.replaceAll("-", "_"))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", fieldValue).addJavadoc("$L\n", comment)
                    .build();
        }
        return fieldSpec;
    }

    public static String getConfigFileSuffix(String configPath) {
        configPath = configPath.substring(0, configPath.lastIndexOf(".json"));
        String[] strings;
        if (configPath.contains("/")) {
            strings = configPath.split("/");
        } else if (configPath.contains("\\")) {
            strings = configPath.split("\\\\");
        } else {
            strings = new String[]{configPath};
        }

        String string = strings[strings.length - 1];
        if (string.startsWith("model-")) {
            string = string.replaceFirst("model-", "");
        }

        return generateName(string);
    }

    private static String processSystemChar(String text) {
        if (text.startsWith("$")) {
            text = text.replaceFirst(text.substring(1, 2), text.substring(1, 2).toUpperCase());
            return text.replaceAll("\\$", "System");
        }
        return text;
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

    private static MethodSpec.Builder generateSetterMethod(Type type, String parameter) {
        String methodName = "set" + generateName(parameter);
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, parameter)
                .addStatement("this.$L = $L", parameter, parameter);
    }
}
