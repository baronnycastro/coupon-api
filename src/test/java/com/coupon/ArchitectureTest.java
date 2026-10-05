package com.coupon;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.coupon", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPure = noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application..", "..adapter..", "..bootstrap..", "org.springframework..", "jakarta..",
                    "org.hibernate..", "com.fasterxml..");

    @ArchTest
    static final ArchRule applicationDoesNotKnowTheOutsideWorld = noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..adapter..", "..bootstrap..", "org.springframework..", "jakarta..", "org.hibernate..",
                    "com.fasterxml..");

    @ArchTest
    static final ArchRule webAdaptersDependOnInputPorts = noClasses().that().resideInAPackage("..adapter.in.web..")
            .should().dependOnClassesThat().resideInAPackage("..application.usecase..");

    @ArchTest
    static final ArchRule useCasesExposeOnlyExecute = classes().that().resideInAPackage("..application.usecase..")
            .and().haveSimpleNameEndingWith("UseCase")
            .should(haveASinglePublicMethodNamedExecute());

    private static ArchCondition<JavaClass> haveASinglePublicMethodNamedExecute() {
        return new ArchCondition<>("have a single public method named execute") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                List<JavaMethod> publicMethods = javaClass.getMethods().stream()
                        .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
                        .toList();
                boolean ok = publicMethods.size() == 1 && publicMethods.get(0).getName().equals("execute");
                events.add(new SimpleConditionEvent(javaClass, ok,
                        javaClass.getName() + " public methods: " + publicMethods));
            }
        };
    }
}
