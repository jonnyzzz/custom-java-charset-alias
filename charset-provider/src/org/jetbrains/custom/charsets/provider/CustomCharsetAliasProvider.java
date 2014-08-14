package org.jetbrains.custom.charsets.provider;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.*;

/**
 * Created 14.08.2014 15:04
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class CustomCharsetAliasProvider extends java.nio.charset.spi.CharsetProvider {
  private static final Logger LOG = new Logger();
  private final Map<String, Charset> myNameToProxy = new TreeMap<String, Charset>();

  public CustomCharsetAliasProvider() {
    final Enumeration<URL> resources;
    try {
      resources = getClass().getClassLoader().getResources("custom-charsets-aliases.properties");
    } catch (IOException e) {
      LOG.log("Failed to list encodings from resources. " + e.getMessage(), e);
      return;
    }

    while(resources.hasMoreElements()) {
      final URL resource = resources.nextElement();

      final Properties ps = new Properties();
      InputStream is = null;
      try {
        is = resource.openStream();
        ps.load(is);
      } catch (IOException e) {
        LOG.log("Failed to open resource connection to " + resource);
        close(is);
      }

      for (String alias : ps.stringPropertyNames()) {
        final String target = ps.getProperty(alias);

        try {
          final Charset aliasCharset = createCharset(alias, target);
          myNameToProxy.put(alias, aliasCharset);
        } catch (Throwable t) {
          LOG.log("Failed to load charset: " + target + " for alias " + alias + ". " + t.getMessage(), t);
        }
      }
    }
  }

  private Charset createCharset(String alias, String target) {
    final Charset targetCharset = Charset.forName(target);
    return new Charset(alias, new String[0]) {
      @Override
      public boolean contains(Charset cs) {
        return targetCharset.contains(cs);
      }

      @Override
      public CharsetDecoder newDecoder() {
        return targetCharset.newDecoder();
      }

      @Override
      public CharsetEncoder newEncoder() {
        return targetCharset.newEncoder();
      }
    };
  }

  @Override
  public Iterator<Charset> charsets() {
    return new ArrayList<Charset>(myNameToProxy.values()).iterator();
  }

  @Override
  public Charset charsetForName(String charsetName) {
    return myNameToProxy.get(charsetName);
  }

  private static void close(final Closeable c) {
    if (c == null) return;
    try {
      c.close();
    } catch (IOException e) {
      //Do not care
    }
  }
}
