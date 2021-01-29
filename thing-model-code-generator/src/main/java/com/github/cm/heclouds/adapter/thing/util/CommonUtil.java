package com.github.cm.heclouds.adapter.thing.util;

import com.google.gson.Gson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;

import javax.lang.model.element.Modifier;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 通用工具
 */
public final class CommonUtil {
    private CommonUtil() {
    }

    public static final String API_EXTENSION_PACKAGE = "com.github.cm.heclouds.adapter.api";

    public static final ClassName CLASS_ADAPTER_API = ClassName.get(CommonUtil.API_EXTENSION_PACKAGE, "AdapterApi");
    public static final ClassName CLASS_DEVICE = ClassName.get("com.github.cm.heclouds.adapter.core.entity", "Device");
    public static final ClassName CLASS_PROPERTY_SET_REQUEST_LISTENER = ClassName.get("com.github.cm.heclouds.adapter.api", "DevicePropertySetListener");
    public static final ClassName CLASS_SERVICE_INVOKE_REQUEST_LISTENER = ClassName.get("com.github.cm.heclouds.adapter.api", "DeviceServiceInvokeListener");

    public static final Gson GSON = new Gson();

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

    public static String generateGenerateJsonObjectMethodName(String text, String suffix) {
        return generateGenerateMethodName(text, suffix) + "JsonObject";
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

    public static String generateReplyMethodName(String text, String suffix) {
        String[] words = processSystemChar(text).split("[_\\-]");
        StringBuilder sb = new StringBuilder("reply");
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

        return "Thing" + generateName(string);
    }

    private static String processSystemChar(String text) {
        if (text.startsWith("$")) {
            text = text.replaceFirst(text.substring(1, 2), text.substring(1, 2).toUpperCase());
            return text.replaceAll("\\$", "System");
        }
        return text;
    }
}
