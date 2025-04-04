package com.github.forax.lilijs.perf;

import com.github.forax.lilijs.Interpreter;
import com.github.forax.lilijs.JSFunction;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.util.concurrent.TimeUnit;

// run benchmark using
// java -cp ./target/lilijs-0.1.0.jar org.openjdk.jmh.Main

// macOS macbook air M2
// Benchmark            Mode  Cnt     Score    Error  Units
// LoopBench.boxedLoop  avgt    5  4559.639 ± 21.029  ns/op
// LoopBench.javaLoop   avgt    5  4802.063 ± 29.899  ns/op
// LoopBench.jsLoop     avgt    5  4643.356 ± 29.041  ns/op

// Linux 6.1.7-gentoo-x86_64 SMP PREEMPT_DYNAMIC x86_64 Intel(R) Xeon(R) Gold 5218 CPU @ 2.30GHz
// Benchmark            Mode  Cnt     Score     Error  Units
// LoopBench.boxedLoop  avgt    5  5416.553 ± 196.887  ns/op
// LoopBench.javaLoop   avgt    5  4668.985 ±  83.428  ns/op
// LoopBench.jsLoop     avgt    5  5191.378 ± 307.627  ns/op

@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "--enable-native-access=ALL-UNNAMED" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LoopBench {

  // function jsLoop() {
  //          let value = 0;
  //          for(let i = 0; i < 1000; i++) {
  //            value = (value * 17 + i) % 1773;
  //          }
  //          return value;
  //        }
  //
  //        return jsLoop;
  private static final MethodHandle JS_LOOP;
  static {
    var jsLoop = (JSFunction) Interpreter.interpret("""
        function jsLoop() {
          let value = 0;
          let i = 0;
          while(i < 1000) {
            value = (value * 17 + i) % 1773;
            i = i + 1;
          }
          return value;
        }
        return jsLoop;
        
        //console.time('jsLoop');
        //jsLoop();
        //console.timeEnd('jsLoop');
        """, System.err);
    JS_LOOP = jsLoop.methodHandle();
    System.err.println(new LoopBench().javaLoop());
    try {
      System.err.println(new LoopBench().jsLoop());
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable t) {
      throw new AssertionError(t);
    }
  }

  @Benchmark
  public int javaLoop() {
    var value = 0;
    for(var i = 0; i < 1000; i++) {
      value = (value * 17 + i) % 1773;
    }
    return value;
  }

  @Benchmark
  public Object boxedLoop() {
    var value = (Integer) 0;
    for(var i = (Integer) 0; i < Integer.valueOf(1000); i++) {
      value = Integer.valueOf(Integer.valueOf(value * 17) + i) % 1773;
    }
    return value;
  }

  @Benchmark
  public Object jsLoop() throws Throwable {
    return JS_LOOP.invokeExact((Object) null);
  }
}
