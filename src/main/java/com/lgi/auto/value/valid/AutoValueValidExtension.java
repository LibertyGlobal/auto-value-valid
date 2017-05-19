package com.lgi.auto.value.valid;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gabrielittner.auto.value.util.AutoValueUtil.newTypeSpecBuilder;

@AutoService(AutoValueExtension.class)
public class AutoValueValidExtension extends AutoValueExtension {

    @Override
    public boolean applicable(Context context) {
        return filteredAbstractMethods(context).size() > 0;
    }

    @Override
    public String generateClass(
            Context context, String className, String classToExtend, boolean isFinal) {
        TypeSpec subclass = newTypeSpecBuilder(context, className, classToExtend, isFinal)
                        .addMethods(generateMethods(context))
                        .build();

        return JavaFile.builder(context.packageName(), subclass).build().toString();
    }

    private List<MethodSpec> generateMethods(Context context) {
        Collection<ExecutableElement> validGetters = filteredAbstractMethods(context);

        List<MethodSpec> generatedMethods = new ArrayList<MethodSpec>(validGetters.size());
        for (ExecutableElement executableElement : validGetters) {
            generatedMethods.add(generateValidGetter(executableElement));
        }
        return generatedMethods;
    }

    private MethodSpec generateValidGetter(
            ExecutableElement executableElement) {

        List<AnnotationSpec> annotations = new ArrayList<AnnotationSpec>(executableElement.getAnnotationMirrors().size() + 1);
        for (AnnotationMirror methodAnnotation : executableElement.getAnnotationMirrors()) {
            annotations.add(AnnotationSpec.get(methodAnnotation));
        }

        List<Modifier> modifiers = new ArrayList<Modifier>(1);
        for (Modifier modifier : executableElement.getModifiers()) {
            if (modifier == Modifier.PUBLIC || modifier == Modifier.PROTECTED) {
                modifiers.add(modifier);
                break;
            }
        }

        return MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addAnnotation(Valid.class)
                .addAnnotation(Override.class)
                .addAnnotations(annotations)
                .addModifiers(modifiers)
                .returns(ClassName.get(executableElement.getReturnType()))
                .addCode(String.format("return super.%s();\n", executableElement.getSimpleName()))
                .build();
    }

    private static ImmutableSet<ExecutableElement> filteredAbstractMethods(Context context) {
        Set<ExecutableElement> abstractMethods = context.abstractMethods();
        ImmutableSet.Builder<ExecutableElement> validGetters = ImmutableSet.builder();
        for (ExecutableElement method : abstractMethods) {
            if (method.getAnnotation(AutoValid.class) != null
                    && method.getParameters().size() == 0) {
                validGetters.add(method);
            }
        }
        return validGetters.build();
    }
}
