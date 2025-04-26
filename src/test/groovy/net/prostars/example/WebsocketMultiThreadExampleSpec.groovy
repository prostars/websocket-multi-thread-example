package net.prostars.example;

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification;

@SpringBootTest(classes = WebsocketMultiThreadExample)
class WebsocketMultiThreadExampleSpec extends Specification {

    void contextLoads() {
        expect:
        true
    }
}
