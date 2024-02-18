package hg.reserve_buy.stockserviceapi.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributionLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransactional aopForTransactional;

    @Around("@annotation(hg.reserve_buy.stockserviceapi.infrastructure.lock.DistributionLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributionLock distributionLock = method.getAnnotation(DistributionLock.class);

        String key = generateKey(joinPoint, signature, distributionLock);
        RLock rLock = redissonClient.getLock(key);

        try {
            log.info("Distribution Lock Key [{}]", key);
            boolean available = acquireLock(distributionLock, rLock);

            if (!available) {
                return false;
            }

            return aopForTransactional.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw e;
        } finally {
            try {
                log.info("Distribution Unlock Key [{}]", key);
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock method {} | key {}", method.getName(), key);
            }
        }
    }

    private boolean acquireLock(DistributionLock distributionLock, RLock rLock) throws InterruptedException {
        return rLock.tryLock(distributionLock.waitTime(), distributionLock.leaseTime(), distributionLock.timeUnit());
    }

    private String generateKey(ProceedingJoinPoint joinPoint, MethodSignature signature, DistributionLock distributionLock) {
        return REDISSON_LOCK_PREFIX + distributionLock.prefix() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributionLock.key());
    }
}
