package com.example.concurrencytest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class SimpleConcurrency {

    private static AtomicLong count = new AtomicLong(0);

    @Test
    void threadNotSafe() throws Exception {
        int maxCnt = 1000;

        for (int i = 0; i < maxCnt; i++) {
            new Thread(() -> {
                count.incrementAndGet();
                System.out.println(count);
            }).start();
        }

        Thread.sleep(100); // 모든 스레드가 종료될 때까지 잠깐 대기
        Assertions.assertThat(count).isEqualTo(maxCnt);
    }


    @Test
    void threadSafeWithSynchronized() throws Exception {
        int maxCnt = 10;

        for (int i = 0; i < maxCnt; i++) {
            new Thread(this::plusSyncronize).start();
        }

        Thread.sleep(100); // 모든 스레드가 종료될 때까지 잠깐 대기
        Assertions.assertThat(count).isEqualTo(maxCnt);
    }

    @Test
    void threadSafeWithExecutorService() throws InterruptedException {
        int maxCnt = 10;
        // 스레드 풀을 생성합니다. 스레드 수는 작업의 양과 시스템 자원에 따라 조정할 수 있습니다.
        ExecutorService executorService = Executors.newFixedThreadPool(maxCnt);

        // 스레드를 제출합니다.
        for (int i = 0; i < maxCnt; i++) {
            executorService.submit(this::plusSyncronize);
        }

        // ExecutorService가 종료될 때까지 대기합니다.
        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(1, TimeUnit.MINUTES);

        if (!terminated) {
            throw new RuntimeException("ExecutorService did not terminate in the specified time.");
        }

        Assertions.assertThat(count).isEqualTo(maxCnt);
    }

    @Test
    void threadNonSafeWithNonSynchronized() throws Exception {
        int maxCnt = 1000;

        for (int i = 0; i < maxCnt; i++) {
            new Thread(this::plus).start();
        }

        Thread.sleep(100); // 모든 스레드가 종료될 때까지 잠깐 대기
        Assertions.assertThat(count).isEqualTo(maxCnt);
    }

    @Test
    void threadSafeWithSynchronizedBlock() throws Exception {
        int maxCnt = 1000;

        for (int i = 0; i < maxCnt; i++) {
            new Thread(this::plusSyncronizeWithSleep).start();
        }

        Thread.sleep(1000); // 모든 스레드가 종료될 때까지 잠깐 대기
        Assertions.assertThat(count).isEqualTo(maxCnt);
    }

    public void plus() {
        count.incrementAndGet();
        System.out.println(count);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void plusSyncronizeWithSleep() { // synchronized 키워드 사용
        count.incrementAndGet();
        System.out.println(count);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void plusSyncronize() { // synchronized 키워드 사용
        count.incrementAndGet();
        System.out.println(count);
    }

}
