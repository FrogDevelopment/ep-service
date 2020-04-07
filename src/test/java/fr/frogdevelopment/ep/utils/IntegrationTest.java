package fr.frogdevelopment.ep.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Tag("integrationTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public abstract class IntegrationTest {

}
