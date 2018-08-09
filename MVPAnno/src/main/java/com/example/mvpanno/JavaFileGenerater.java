package com.example.mvpanno;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

public class JavaFileGenerater {

    public static void outputFile(String packageName, TypeSpec spec) {
        JavaFile file = JavaFile.builder(packageName, spec).build();
    }

    public static TypeSpec clazz(FieldSpec builtinTypeField, FieldSpec arrayTypeField, FieldSpec refTypeField,
                                 FieldSpec typeField, FieldSpec parameterizedTypeField, FieldSpec wildcardTypeField,
                                 MethodSpec constructor, MethodSpec methodSpec) {
        return TypeSpec.classBuilder("Clazz")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addTypeVariable(TypeVariableName.get("T"))

                .superclass(String.class)
                .addSuperinterface(Serializable.class)
                .addSuperinterface(ParameterizedTypeName.get(Comparable.class, String.class))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Map.class),
                        TypeVariableName.get("T"),
                        WildcardTypeName.subtypeOf(String.class)))

                .addStaticBlock(CodeBlock.builder().build())
                .addInitializerBlock(CodeBlock.builder().build())

                .addField(builtinTypeField)
                .addField(arrayTypeField)
                .addField(refTypeField)
                .addField(typeField)
                .addField(parameterizedTypeField)
                .addField(wildcardTypeField)

                .addMethod(constructor)
                .addMethod(methodSpec)

                .addType(TypeSpec.classBuilder("InnerClass").build())

                .build();
    }
    public static FieldSpec builtinTypeField() {
        return FieldSpec.builder(int.class, "mInt", Modifier.PRIVATE).build();
    }

    public static FieldSpec arrayTypeField() {
        return FieldSpec.builder(int[].class, "mArr", Modifier.PRIVATE).build();
    }


    public static FieldSpec refTypeField() {
        return FieldSpec.builder(File.class, "mRef", Modifier.PRIVATE).build();
    }


    public static FieldSpec typeField() {
        return FieldSpec.builder(TypeVariableName.get("T"), "mT", Modifier.PRIVATE).build();
    }


    public static FieldSpec parameterizedTypeField() {
        return FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class),
                "mParameterizedField",
                Modifier.PRIVATE)
                .build();
    }

    public static FieldSpec wildcardTypeField() {
        return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
                WildcardTypeName.subtypeOf(String.class)),
                "mWildcardField",
                Modifier.PRIVATE)
                .build();
    }

    public static MethodSpec constructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    public static MethodSpec method(CodeBlock codeBlock) {
        return MethodSpec.methodBuilder("method")
                .addAnnotation(Override.class)
                .addTypeVariable(TypeVariableName.get("T"))
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addParameter(String.class, "string")
                .addParameter(TypeVariableName.get("T"), "t")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(Integer.class),
                        WildcardTypeName.subtypeOf(TypeVariableName.get("T"))),
                        "map")
                .addException(IOException.class)
                .addException(RuntimeException.class)
                .addCode(codeBlock)
                .build();
    }

    public static CodeBlock code() {
        return CodeBlock.builder()
                .addStatement("int foo = 1")
                .addStatement("$T bar = $S", String.class, "a string")

                .addStatement("$T obj = new $T(5)",
                        Object.class, ParameterizedTypeName.get(ClassName.get(HashMap.class),
                                ClassName.get(Integer.class),
                                WildcardTypeName.subtypeOf(TypeVariableName.get("T"))))


                .addStatement("baz($L)", TypeSpec.anonymousClassBuilder("$T param", String.class)
                        .superclass(Runnable.class)
                        .addMethod(MethodSpec.methodBuilder("run")
                                .addAnnotation(Override.class)
                                .returns(TypeName.VOID)
                                .build())
                        .build())

                .beginControlFlow("for (int i = 0; i < 5; i++)")
                .endControlFlow()

                .beginControlFlow("while (false)")
                .endControlFlow()

                .beginControlFlow("do")
                .endControlFlow("while (false)")

                .beginControlFlow("if (false)")
                .nextControlFlow("else if (false)")
                .nextControlFlow("else")
                .endControlFlow()

                .beginControlFlow("try")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("e.printStackTrace()")
                .nextControlFlow("finally")
                .endControlFlow()

                .addStatement("return 0")
                .build();
    }
}
