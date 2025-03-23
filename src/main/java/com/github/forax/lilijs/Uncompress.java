package com.github.forax.lilijs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Uncompress {
  public static void main(String[] args) throws ClassNotFoundException, IOException {
    var start = System.currentTimeMillis();
    String libName = "libswc4j-macos-arm64.v.1.3.0.dylib";
    URL url = Class.forName("com.caoccao.javet.swc4j.Swc4jNative").getResource("/" + libName);
    Path tmpDir = Files.createTempDirectory("swc4j-native");
    tmpDir.toFile().deleteOnExit();
    Path nativeLibTmpFile = tmpDir.resolve(libName);
    nativeLibTmpFile.toFile().deleteOnExit();
    try (InputStream in = url.openStream()) {
      Files.copy(in, nativeLibTmpFile);
    }
    System.load(nativeLibTmpFile.toAbsolutePath().toString());
    var end = System.currentTimeMillis();
    System.err.println((end - start) + " ms");
  }
}
