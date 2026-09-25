package ru.alfabank.practice.chulyukovnv.bankonboarding.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Test
    void aroundLogging_shouldProceedAndReturnResult() throws Throwable {
        String expectedResult = "Успешный ответ";
        Object[] args = new Object[]{"аргумент1", 123};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestClass.testMethod");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object actualResult = loggingAspect.aroundLogging(joinPoint);

        assertEquals(expectedResult, actualResult); // Убеждаемся, что результат не потерялся
        verify(joinPoint, times(1)).proceed();      // Убеждаемся, что оригинальный метод был вызван ровно один раз
    }

    @Test
    void aroundLogging_shouldThrowException_whenProceedFails() throws Throwable {
        RuntimeException expectedException = new RuntimeException("Test error");
        Object[] args = new Object[]{};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("TestClass.testMethod");
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> loggingAspect.aroundLogging(joinPoint));

        verify(joinPoint, times(1)).proceed();
    }
}
