package org.jetbrains.custom.charsets.provider;

/**
 * We do not like to introduce any logger dependencies for
 * not, but using Java's standard logging is also not the
 * case.
 * <p/>
 * So we have this smelly code for now
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
class Logger {
  private static final String PREFIX = "[CUSTOM CHARSETS] ";

  private boolean isEnabled() {
    return System.getProperty("org.jetbrains.custom.charsets.provider.LOG") != null;
  }

  public void log(String message) {
    log(message, null);
  }

  public void log(String message, Throwable exception) {
    if (message == null) return;
    if (!isEnabled()) return;
    System.out.println(PREFIX + message);
    if (exception != null) {
      exception.printStackTrace();
    }
  }
}
