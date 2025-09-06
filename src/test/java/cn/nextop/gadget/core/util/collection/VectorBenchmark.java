package cn.nextop.gadget.core.util.collection;

import static jdk.incubator.vector.ByteVector.SPECIES_128;
import static jdk.incubator.vector.ByteVector.SPECIES_256;
import static jdk.incubator.vector.ByteVector.SPECIES_64;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * @author Baoyi Chen
 * 
 * JDK21
 * Benchmark                          Mode  Cnt           Score           Error  Units
 * VectorBenchmark.benchVector64     thrpt    5  1107103300.758 ±  33339003.053  ops/s
 * VectorBenchmark.benchBaseline64   thrpt    5   853237985.581 ±  25171455.813  ops/s
 *
 * VectorBenchmark.benchVector128    thrpt    5   856022902.859 ±  29976677.030  ops/s
 * VectorBenchmark.benchBaseline128  thrpt    5   480960649.009 ±   9947239.233  ops/s
 *
 * VectorBenchmark.benchVector256    thrpt    5   602314749.130 ± 123344099.808  ops/s
 * VectorBenchmark.benchBaseline256  thrpt    5   281658513.371 ±  38389000.790  ops/s
 * 
 * JDK17
 * Benchmark                          Mode  Cnt          Score          Error  Units
 * VectorBenchmark.benchVector64     thrpt    5  195674139.000 ± 21655784.405  ops/s
 * VectorBenchmark.benchBaseline64   thrpt    5  813230113.632 ± 59593956.526  ops/s
 *
 * VectorBenchmark.benchVector128    thrpt    5  108505940.431 ±  5986180.381  ops/s
 * VectorBenchmark.benchBaseline128  thrpt    5  443073320.796 ±  6771567.201  ops/s
 *
 * VectorBenchmark.benchVector256    thrpt    5   73933968.663 ±  9164903.017  ops/s
 * VectorBenchmark.benchBaseline256  thrpt    5  284460079.313 ± 15164997.262  ops/s
 */
@Fork(value = 1, jvmArgsAppend = {
		"--add-modules=jdk.incubator.vector",
		"--enable-preview"
})
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class VectorBenchmark {
	
	public static long matchH2(int offset, byte h2, byte[] data, VectorSpecies<Byte> species) {
		ByteVector v = ByteVector.fromArray(species, data, offset);
		return v.eq(h2).toLong();
	}
	
	public byte[] data;
	
	@Setup
	public void setup() {
		data = new byte[64];
		Arrays.fill(data, (byte)18);
		data[3] = 1;
		data[12] = 1;
		data[31] = 1;
		data[33] = 1;
		data[63] = 1;
	}
	
	@Benchmark
	public int benchVector64() {
		long v = matchH2(0, (byte)1, data, SPECIES_64);
		int r = -1;
		while (v != 0) {
			int n = Long.numberOfTrailingZeros(v);
			r = n;
			v &= ~(1L << n);
		}
		return r;
	}
	
	@Benchmark
	public int benchVector128() {
		long v = matchH2(0, (byte)1, data, SPECIES_128);
		int r = -1;
		while (v != 0) {
			int n = Long.numberOfTrailingZeros(v);
			r = n;
			v &= ~(1L << n);
		}
		return r;
	}
	
	@Benchmark
	public int benchVector256() {
		long v = matchH2(0, (byte)1, data, SPECIES_256);
		int r = -1;
		while (v != 0) {
			int n = Long.numberOfTrailingZeros(v);
			r = n;
			v &= ~(1L << n);
		}
		return r;
	}
	
	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(VectorBenchmark.class.getSimpleName()).shouldDoGC(true)
				.build();
		new Runner(opt).run();
	}
}
