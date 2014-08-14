import java.io.UnsupportedEncodingException;

/**
 * This main class will not works from IntelliJ, since it uses a java wrapper that changes
 * system classloader making application classed not being loaded there
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class SmokeTest {
  //There is only 1 test for now, I do not see the need to include test-ng or junit
  public static void main(String[] args) throws UnsupportedEncodingException {
    try {
      ClassLoader.getSystemClassLoader().loadClass("org.jetbrains.custom.charsets.provider.CustomCharsetAliasProvider");
    } catch (Throwable t) {
      System.err.println("Our jar must be in system classpath, not in some other classloader.");
      System.out.println();
    }

    //see into the bundled custom-charsets-aliases.properties
    testAlias("System", "UTF-16");

    //see into the bundled custom-charsets-aliases.properties
    testAlias("X-test", "UTF-8");
  }

  private static void testAlias(String alias, String dest) throws UnsupportedEncodingException {
    final String text = "hohoho from " + dest + " into " + alias;
    final byte[] bytes = text.getBytes(dest);

    System.out.println(alias + " => " + dest);
    if (!text.equals(new String(bytes, alias))) {
      System.err.println("  Failed to trick with '" + alias + "' encoding as '" + dest + "'");
    } else {
      System.out.println("  OK");
    }
    System.out.println();
  }
}
