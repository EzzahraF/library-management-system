package com.ensam.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class LibraryManagementApplicationTests {

    @Test
    void contextLoads() {
        // Test que le contexte Spring Boot se charge correctement
        // Si ce test passe, cela signifie que toutes les beans sont correctement configurées
    }
}
