package com.example.mvpanno.processor;

import com.example.mvpanno.anno.MVPAnno;
import com.example.mvpanno.anno.MVPAnnoType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes(value = {"com.example.mvpanno.anno.MVPAnno",
        "com.example.mvpanno.anno.GetMVPCom"})
public class MVPAnnoProcessor extends AbstractProcessor{

    private static final String NAME = "Support$MVPAnno";
    private static final String packageName = "annotationlearn";

    private Map<String, String> map = new HashMap<>();

    private Messager messager;
    private Filer filer;
    private Types typeUtils;

    private boolean hasCreated;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.out.println("processor init!!!");
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        filer = processingEnvironment.getFiler();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(MVPAnno.class)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "processorworking : " + element.toString());
            MVPAnno anno = element.getAnnotation(MVPAnno.class);
            if (anno != null) {
                String bussiness = anno.bussiness();
                MVPAnnoType type = anno.type();
                String stype = "_";
                switch (type) {
                    case VIEW:
                        stype = "_view";
                        break;
                    case MODEL:
                        stype = "_model";
                        break;
                    case PRESENTER:
                        stype = "_presenter";
                        break;
                    default:
                        break;
                }
                String key = bussiness + stype;
                map.put(key, element.toString());
            }
        }
        generateJavaFile(packageName);
        return false;
    }

    private PackageElement getPackage(Element type) {
        while (type.getKind() != ElementKind.PACKAGE) {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @SuppressWarnings("unchecked")
    private static Set<String> arrayToSet(String[] array) {
        if (array == null || array.length == 0) {
            return new HashSet<>(0);
        }
        HashSet set = new HashSet(array.length);
        set.addAll(Arrays.asList(array));
        return Collections.unmodifiableSet(set);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    private void generateJavaFile(String packageName) {

        if (hasCreated) {
            return;
        }
        hasCreated = true;

        ParameterizedTypeName m = ParameterizedTypeName.get(
                ClassName.get(HashMap.class), ClassName.get(String.class),
                ClassName.get(String.class));
        FieldSpec map = FieldSpec.builder(m, "map", Modifier.FINAL,
                Modifier.STATIC, Modifier.PRIVATE).build();

        CodeBlock.Builder statBuilder = CodeBlock.builder();
        statBuilder.addStatement("map = new HashMap<String, String>()");
        for (String key : this.map.keySet()) {
            statBuilder.addStatement("map.put($S, $S)", key, this.map.get(key));
        }

        CodeBlock.Builder consBuilder = CodeBlock.builder();
        MethodSpec cons = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(consBuilder.build())
                .build();

        CodeBlock.Builder getBuilder = CodeBlock.builder();
        getBuilder.addStatement("String stype = null");
        getBuilder.beginControlFlow("if (type == $T.VIEW) ", MVPAnnoType.class)
                .addStatement("stype = bussiness + " + '"' + "_view" + '"')
                .nextControlFlow("else if (type == $T.PRESENTER)", MVPAnnoType.class)
                .addStatement("stype = bussiness + " + '"' + "_presenter" + '"')
                .nextControlFlow("else if (type == $T.MODEL)", MVPAnnoType.class)
                .addStatement("stype = bussiness + " + '"' + "_model" + '"')
                .nextControlFlow("else")
                .addStatement("stype = bussiness + " + '"' + "_" + '"')
                .endControlFlow();

        getBuilder.addStatement("return map.get(stype)");

        MethodSpec get = MethodSpec.methodBuilder("getClass")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(String.class, "bussiness")
                .addParameter(MVPAnnoType.class, "type")
                .addCode(getBuilder.build())
                .returns(String.class)
                .build();

        TypeSpec spec = TypeSpec.classBuilder(NAME)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addField(map)
                .addStaticBlock(statBuilder.build())
                .addMethod(cons)
                .addMethod(get)
                .build();

        JavaFile file = JavaFile.builder(packageName, spec)
                .addFileComment(" These codes are generated automatically. Do not modify!")
                .build();
        note(packageName + NAME);
        try {
            file.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            error("processor write to file failed");
        }
    }

    private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
                                            Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }

        return false;
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void error(String message) {
        printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void note(String message) {
        printMessage(Diagnostic.Kind.NOTE, message);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(kind, message, element);
    }

    private void printMessage(Diagnostic.Kind kind, String message) {
        processingEnv.getMessager().printMessage(kind, message);
    }
}
