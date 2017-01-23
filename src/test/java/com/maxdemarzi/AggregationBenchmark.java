package com.maxdemarzi;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class AggregationBenchmark {

    private static Disoriented db;
    private Random rand = new Random();

    @Setup(Level.Invocation )
    public void prepare() throws IOException {
        Disoriented.init();
        db = Disoriented.getInstance();

        for (int person = 0; person < 1632803; person++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id" + person, "id" + person);
            properties.put("age", rand.nextInt(120));
            db.addNode("id" + person, properties);
        }
    }


    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(4)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureAggregation() throws IOException {
        Iterator<PropertyContainer> iter = db.getAllNodes();
        HashMap<Integer, Integer> ages = new HashMap<>();
        Integer age;
        while (iter.hasNext()) {
            PropertyContainer nodeEntry = iter.next();
            age = (Integer) nodeEntry.getProperties().get("age");
            ages.merge(age, 1, Integer::sum);
        }
    }

}
