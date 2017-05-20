import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.TRACE

appender("STDOUT", ConsoleAppender) {
    layout(PatternLayout) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}
appender("FILE", FileAppender) {
    file = "JuliaServer.log"
    layout(PatternLayout) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}
logger("com.miskevich.webserver", TRACE)
root(DEBUG, ["STDOUT", "FILE"])
