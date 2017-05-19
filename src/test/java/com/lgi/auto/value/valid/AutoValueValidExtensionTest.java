package com.lgi.auto.value.valid;

import com.google.auto.value.AutoValue;
import org.junit.Test;
import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;
import java.util.Collections;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AutoValueValidExtensionTest {

    @Test
    public void addValidToGeneratedMethod() {
            JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
                    + "package test;\n"
                    + "import com.google.auto.value.AutoValue;\n"
                    + "import com.lgi.auto.value.valid.AutoValid;\n"
                    + "@AutoValue public abstract class Test {\n"
                    + "  @AutoValid public abstract String a();\n"
                    + "}\n");

            JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/AutoValue_Test", ""
                    + "package test;\n"
                    + "import com.lgi.auto.value.valid.AutoValid;\n"
                    + "import java.lang.Override;\n"
                    + "import java.lang.String;\n"
                    + "import javax.validation.Valid;\n"
                    + "final class AutoValue_Test extends $AutoValue_Test {\n"
                    + "  AutoValue_Test(String a) {\n"
                    + "    super(a);\n"
                    + "  }\n"
                    + "  @Valid @Override @AutoValid public String a() {\n"
                    + "    return super.a();\n"
                    + "  }\n"
                    + "}\n");

            assertAbout(javaSources())
                    .that(Collections.singletonList(source))
                    .processedWith(new AutoValueProcessor())
                    .compilesWithoutError()
                    .and()
                    .generatesSources(expectedSource);
    }
}
