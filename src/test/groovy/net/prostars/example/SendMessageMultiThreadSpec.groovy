package net.prostars.example


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(
        classes = WebsocketMultiThreadExample,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendMessageMultiThreadSpec extends Specification {

    @LocalServerPort
    int port
    
    def clientA, clientB, clientC
    
    def cleanup() {
        clientA.session?.close()
        clientB.session?.close()
        clientC.session?.close()
    }

    def 'Group Chat Basic Test'() {
        given:
        def url = "ws://localhost:${port}/ws/v1/message"
        (clientA, clientB, clientC) = [createClint(url), createClint(url), createClint(url)]

        when:
        clientA.session.sendMessage(new TextMessage('clientA: 안녕하세요. A 입니다.'))
        clientB.session.sendMessage(new TextMessage('clientB: 안녕하세요. B 입니다.'))
        clientC.session.sendMessage(new TextMessage('clientC: 안녕하세요. C 입니다.'))

        then:
        def result = (0..1).collect { clientA.queue.poll(1, TimeUnit.SECONDS) }
        result << (0..1).collect { clientB.queue.poll(1, TimeUnit.SECONDS) }
        result << (0..1).collect { clientC.queue.poll(1, TimeUnit.SECONDS) }
        
        and:
        result.contains(null)
    }

    def 'Group Chat Concurrent Test'() {
        given:
        def url = "ws://localhost:${port}/ws/v2/message"
        (clientA, clientB, clientC) = [createClint(url), createClint(url), createClint(url)]

        when:
        clientA.session.sendMessage(new TextMessage('clientA: 안녕하세요. A 입니다.'))
        clientB.session.sendMessage(new TextMessage('clientB: 안녕하세요. B 입니다.'))
        clientC.session.sendMessage(new TextMessage('clientC: 안녕하세요. C 입니다.'))

        then:
        def resultA = (0..1).findResults { clientA.queue.poll(1, TimeUnit.SECONDS) }.join('')
        def resultB = (0..1).findResults { clientB.queue.poll(1, TimeUnit.SECONDS) }.join('')
        def resultC = (0..1).findResults { clientC.queue.poll(1, TimeUnit.SECONDS) }.join('')
        resultA.contains('clientB') && resultA.contains('clientC')
        resultB.contains('clientA') && resultB.contains('clientC')
        resultC.contains('clientA') && resultC.contains('clientB')

        and:
        clientA.queue.isEmpty()
        clientB.queue.isEmpty()
        clientC.queue.isEmpty()
    }

    static def createClint(String url) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5)
        def client = new StandardWebSocketClient()
        def webSocketSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                blockingQueue.put(message.payload)
            }
        }, url).get()

        [queue: blockingQueue, session: webSocketSession]
    }
}
