package com.notonthehighstreet.concurrent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FunctionalReadWriteLockTest {

    private final String TEST_VALUE = "testValue";

    @Mock
    private ReadWriteLock rwLock;

    private FunctionalReadWriteLock lock;

    @Mock(lenient = true)
    private Lock readLock;
    @Mock(lenient = true)
    private Lock writeLock;
    @Mock(lenient = true)
    private Supplier<String> supplier;
    @Mock
    private Runnable runnable;

    @BeforeEach
    public void setup() {
        given(rwLock.readLock()).willReturn(readLock);
        given(rwLock.writeLock()).willReturn(writeLock);
        lock = new FunctionalReadWriteLock(rwLock);
        given(supplier.get()).willReturn(TEST_VALUE);
    }

    @Test
    public void givenSupplierWhenReadThenGet() {
        //given

        //when
        String value = lock.read(supplier);

        //then
        assertEquals(value, TEST_VALUE);
        InOrder inOrder = inOrder(readLock, supplier);
        inOrder.verify(readLock).lock();
        inOrder.verify(supplier).get();
        inOrder.verify(readLock).unlock();
    }

    @Test
    public void givenSupplierWhenReadAndExceptionThenUnlock() {
        //given
        given(supplier.get()).willThrow(new RuntimeException());

        //when
        assertThrows(RuntimeException.class, () -> lock.read(supplier));

        //then
        InOrder inOrder = inOrder(readLock, supplier);
        inOrder.verify(readLock).lock();
        inOrder.verify(supplier).get();
        inOrder.verify(readLock).unlock();
    }

    @Test
    public void givenRunnableWhenReadThenRun() {
        //given

        //when
        lock.read(runnable);

        //then
        InOrder inOrder = inOrder(readLock, runnable);
        inOrder.verify(readLock).lock();
        inOrder.verify(runnable).run();
        inOrder.verify(readLock).unlock();
    }

    @Test
    public void givenRunnableWhenReadAndExceptionThenUnlock() {
        //given
        willThrow(new RuntimeException()).given(runnable).run();

        //when
        assertThrows(RuntimeException.class, () -> lock.read(runnable));

        //then
        InOrder inOrder = inOrder(readLock, runnable);
        inOrder.verify(readLock).lock();
        inOrder.verify(runnable).run();
        inOrder.verify(readLock).unlock();
    }

    @Test
    public void givenSupplierWhenWriteThenGet() {
        //given

        //when
        String value = lock.write(supplier);

        //then
        assertEquals(value, TEST_VALUE);
        InOrder inOrder = inOrder(writeLock, supplier);
        inOrder.verify(writeLock).lock();
        inOrder.verify(supplier).get();
        inOrder.verify(writeLock).unlock();
    }

    @Test
    public void givenSupplierWhenWriteAndExceptionThenUnlock() {
        //given
        given(supplier.get()).willThrow(new RuntimeException());

        //when
        assertThrows(RuntimeException.class, () -> lock.write(supplier));

        //then
        InOrder inOrder = inOrder(writeLock, supplier);
        inOrder.verify(writeLock).lock();
        inOrder.verify(supplier).get();
        inOrder.verify(writeLock).unlock();
    }

    @Test
    public void givenRunnableWhenWriteThenRun() {
        //given

        //when
        lock.write(runnable);

        //then
        InOrder inOrder = inOrder(writeLock, runnable);
        inOrder.verify(writeLock).lock();
        inOrder.verify(runnable).run();
        inOrder.verify(writeLock).unlock();
    }

    @Test
    public void givenRunnableWhenWriteAndExceptionThenUnlock() {
        //given
        willThrow(new RuntimeException()).given(runnable).run();

        //when
        assertThrows(RuntimeException.class, () -> lock.write(runnable));

        //then
        InOrder inOrder = inOrder(writeLock, runnable);
        inOrder.verify(writeLock).lock();
        inOrder.verify(runnable).run();
        inOrder.verify(writeLock).unlock();
    }

}
