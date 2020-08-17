package com.github.cm.heclouds.adapter.thing.processor;

import com.github.cm.heclouds.adapter.thing.annotations.ThingModelConfiguration;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

@AutoService(Processor.class)
public class OpenApiExtensionProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(ThingModelConfiguration.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ThingModelConfiguration.class);
        for (Element element : elements) {
            if (ElementKind.CLASS == element.getKind()) {
                processProductConfiguration(element);
            }
        }
        return true;
    }

    private void processProductConfiguration(Element element) {
        try {
            String[] configPath = element.getAnnotation(ThingModelConfiguration.class).value();
            for (String path : configPath) {
                ProductProcessor.doProcessProductConfiguration(path, processingEnv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
