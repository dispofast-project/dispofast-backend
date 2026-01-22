package com.dispocol.dispofast;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

public class ArchitectureTest {
    
    private static final String BASE_PACKAGE = "com.dispocol.dispofast";
    private static final String MODULES = BASE_PACKAGE + ".modules..";
    private static final String SHARED = BASE_PACKAGE + ".shared..";

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages(BASE_PACKAGE);

    @Test
    void infra_is_internal_to_its_module() {
        var rule = ArchRuleDefinition.noClasses()
            .that()
                .resideInAPackage(MODULES)
            .should()
                .dependOnClassesThat()
                .resideInAPackage(MODULES.replace("..", "")+ ".infra..");
        
        rule.check(classes);

    }

}
