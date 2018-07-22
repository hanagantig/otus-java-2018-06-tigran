package ru.otus.l021;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Supplier;


@SuppressWarnings({"RedundantStringConstructorCall", "InfiniteLoopStatement"})
public class Main {

    public static void main(String... args) throws InterruptedException {
        printSizeInfo(getObjectSize(() -> new byte[0]), "empty array of bytes"); // 16
        printSizeInfo(getObjectSize(() -> new byte[2]), "array with 2 byte element"); // 16 + 4*2 = 24
        printSizeInfo(getObjectSize(() -> new int[0]), "empty array of ints"); // 16
        printSizeInfo(getObjectSize(() -> new int[1]), "array with 1 int element"); // 16 + 4*1 = 20 => 24
        printSizeInfo(getObjectSize(() -> new String("")), "empty string"); // 16 + 4 = 20 => 24
        printSizeInfo(getObjectSize(() -> new String(new char[0])), "empty string chars"); // 16 + 4 = 20 => 24 Как я пнимаю из за ссылки в String pool
        printSizeInfo(getObjectSize(() -> new String(new char[1])), "string with 1 char"); // 48
        printSizeInfo(getObjectSize(() -> new String(new char[5])), "string with 5 chars"); // 48
        printSizeInfo(getObjectSize(() -> new String(new byte[0])), "empty string bytes"); // 40
        printSizeInfo(getObjectSize(() -> new String(new byte[1])), "string with 1 byte"); // 48 or 47 ?? Это просто погрешность?
        printSizeInfo(getObjectSize(() -> new String(new byte[5])), "string with 5 bytes"); // 48 or 47 ??
        printSizeInfo(getObjectSize(() -> new MyClass<Integer>(0)), "MyClass with int"); // 16
        printSizeInfo(getObjectSize(() -> new MyClass<Boolean>(true)), "MyClass with boolean"); // 16
        printSizeInfo(getObjectSize(() -> new MyClass<Long>(0L)), "MyClass with long"); // 16
    }

    private static void printSizeInfo(long size, String title){
        System.out.println(String.format("Size of %s: %d", title, size ));
    }

    private static long getObjectSize(Supplier<?> supplier) throws InterruptedException {

        int size = 20_000_000;

        long mem = getMem();

        Object[] array = new Object[size];

        long mem2 = getMem();

        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
        }

        long mem3 = getMem();
        long element_size = (mem3 - mem2) / array.length;

        array = null;

        Thread.sleep(1000); //wait for 1 sec
        return element_size;
    }

    private static long getMem() throws InterruptedException {
        System.gc();
        Thread.sleep(10);
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static class MyClass<T> {
        private T value;

        public void setValue(T value) {
            this.value = value;
        }

        public MyClass(T val){
            setValue(val);
        }
    }
}