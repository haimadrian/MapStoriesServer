package org.bravetogether.mapstories.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Main entry to the backend of map stories.<br/>
 * Spring boot will scan the mapstories package in order to lookup for components. Refer to controller package in order
 * to see the controllers and debug the program, or just scan the code from there to understand what we are doing.
 * For example, take a look at {@link org.bravetogether.mapstories.server.controller.UserController}.
 * <p>
 *    We have the XXXController classes that accept the HTTP requests.</br>
 *    Then, we use the XXXService classes in order to connect between the controller layer and the repository layer, with additional logic.<br/>
 *    Then, we have the XXXRepository classes so we can perform CRUD operations in front of our database (MySQL)
 * </p>
 * <p>
 *    Refer to application.properties to see the application properties.<br/>
 *    We depend on environment variables to contain the connection information to mysql, so we do not have to expose this
 *    sensitive information on github.<br/>
 *    The application-test.properties is in use when we run integration tests. So do not delete it.
 * </p>
 * <p>
 *    We use Hibernate with Spring data JPA to represent out database. Hence you'll see some annotations at the DB models.<br/>
 *    In addition, we use project Lombok in order to generate getters/setters/toString etc. automatically based on data members.
 * </p>
 * <p>
 *    I have created a self signed certificate in order to support HTTPS communication.<br/>
 *    It is required to use a real signed certificate instead. Replace bravetogether.p12 and server.crt for that.
 * </p>
 * <p>
 *    We use JWT for authorizing and authenticating users.<br/>
 *    User must first sign up. Then, users can sign in and get a JWT token in response from the server. Client
 *    must use this token as Authorization header, e.g. "Authorization=Bearer sometokenhere" for every request.
 * </p>
 * @author Haim Adrian
 * @since 21-Mar-21
 * @see org.bravetogether.mapstories.server.controller.UserController
 * @see org.bravetogether.mapstories.server.controller.StoryController
 * @see org.bravetogether.mapstories.server.controller.CoordinateController
 */
@SpringBootApplication
public class MapStoriesServerMain {
   private static final String STDOUT_LOGGER_NAME = "stdout";
   private static final String STDERR_LOGGER_NAME = "stderr";

   public static void main(String[] args) {
      configureLog4j2();
      redirectStreamsToLog4j();
      SpringApplication.run(MapStoriesServerMain.class, args);
   }

   private static void configureLog4j2() {
      // Use asynchronous loggers by default for better performance
      System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

      // When the async queue is full, discard all DEBUG and TRACE messages that can not be ingested to the queue. INFO and more descriptive will block the caller
      // thread until there is a space for the log event to be kept.
      System.setProperty("log4j2.AsyncQueueFullPolicy", "Discard");
      System.setProperty("log4j2.DiscardThreshold", "DEBUG");

      // Redirect logs from Java Util Logging to our log4j2, cause there are third parties using JUL.
      System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

      // Set default log directory in case it was not specified outside the application
      if (System.getProperty("org.bravetogether.mapstories.logdir") == null) {
         System.setProperty("org.bravetogether.mapstories.logdir", "C:/BraveTogether/log");
      }
   }

   private static void redirectStreamsToLog4j() {
      System.setOut(new PrintStream(new LoggingStream(STDOUT_LOGGER_NAME), true));
      System.setErr(new PrintStream(new LoggingStream(STDERR_LOGGER_NAME), true));

      System.out.println(getJavaVersionString());
   }

   private static String getJavaVersionString() {
      return "java version \"" + System.getProperty("java.version") + "\"" + System.lineSeparator() + System.getProperty("java.runtime.name") +
            " (build " + System.getProperty("java.runtime.version") + ")" + System.lineSeparator() + System.getProperty("java.vm.name") +
            " (build " + System.getProperty("java.vm.version") + ", " + System.getProperty("java.vm.info") + ")";
   }

   /**
    * This output stream holds all streamed data in an internal buffer. On flush() it will send buffer data to the relevant logger.
    */
   private static class LoggingStream extends OutputStream {
      /**
       * Internal stream buffer
       */
      private final StringBuilder sb;

      /**
       * The logger where we flush internal buffer to
       */
      private final Logger logger;

      /**
       * Constructs a new {@link LoggingStream}
       *
       * @param loggerName Name of the logger to log messages to
       */
      public LoggingStream(String loggerName) {
         sb = new StringBuilder(128);
         logger = LogManager.getLogger(loggerName);
      }

      /**
       * Writes the specified byte to this output stream. The general contract for <code>write</code> is
       * that one byte is written to the output stream. The byte to be written is the eight low-order bits of
       * the argument <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
       * <p>
       * Subclasses of <code>OutputStream</code> must provide an implementation for this method.
       *
       * @param b the <code>byte</code>.
       */
      @Override
      public void write(int b) {
         sb.append((char) b);
      }

      /**
       * Flushes this output stream and forces any buffered output bytes to be written out. The general
       * contract of <code>flush</code> is that calling it is an indication that, if any bytes previously
       * written have been buffered by the implementation of the output stream, such bytes should immediately
       * be written to their intended destination.
       * <p>
       * The <code>flush</code> method of <code>OutputStream</code> does nothing.
       */
      @Override
      public void flush() {
         if (sb.length() > 0) {
            String message = sb.toString();

            // When calling System.out.println, there is the print of the message which is flushed
            // and we print it with a new line as part of the logger implementation, and then there is additional
            // newLine() call of the println, which we would like to ignore cause the logger already prints a new line.
            if (!System.lineSeparator().equals(message)) {
               logger.info(message);
            }

            sb.setLength(0);
         }
      }

      /**
       * Closes this output stream and releases any system resources associated with this stream. The general
       * contract of <code>close</code> is that it closes the output stream. A closed stream cannot perform
       * output operations and cannot be reopened.
       * <p>
       * The <code>close</code> method of <code>OutputStream</code> does nothing.
       */
      @Override
      public void close() {
         flush();
      }
   }
}

