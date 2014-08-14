import java.io.*;
import java.util.Arrays;

/**
 * A workaround to have this working with IntelliJ runner
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class TestLauncher {
  public static void main(String[] args) throws Exception {

    final String java = new File(System.getProperty("java.home"), "bin/java").getAbsolutePath();
    final String production = new File("./out/production/charset-provider").getCanonicalPath();
    final String tests = new File("./out/test/charset-provider").getCanonicalPath();

    final Process process = new ProcessBuilder()
            .command(
                    java,
                    "-cp",
                    production + System.getProperty("path.separator") + tests,
                    "SmokeTest"
            ).start();

    for (final InputStream steam : Arrays.asList(process.getInputStream(), process.getErrorStream())) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          BufferedReader reader = new BufferedReader(new InputStreamReader(steam));
          String line;
          try {
            while((line = reader.readLine()) != null) {
              System.out.println("out: " + line);
            }
          } catch (IOException e) {
            //
          } finally {
            try {
              reader.close();
            } catch (IOException e) {
              //
            }
          }
        }
      }).start();
    }

    process.waitFor();
  }
}
