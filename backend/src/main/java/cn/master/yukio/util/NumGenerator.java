package cn.master.yukio.util;

import cn.master.yukio.constants.ApplicationNumScope;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RIdGenerator;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Created by 11's papa on 02/29/2024
 **/
@Component
public class NumGenerator {
    // 代表从100001开始，各种domain的 num
    private static final long INIT = 100001L;
    private static final long LIMIT = 1;

    private static Redisson redisson;
    private static StringRedisTemplate stringRedisTemplate;
    private static final List<ApplicationNumScope> SUB_NUM = List.of(ApplicationNumScope.API_TEST_CASE, ApplicationNumScope.API_MOCK, ApplicationNumScope.TEST_PLAN_FUNCTION_CASE, ApplicationNumScope.TEST_PLAN_API_CASE, ApplicationNumScope.TEST_PLAN_API_SCENARIO);

    public static long nextNum(String prefix, ApplicationNumScope scope) {
        RIdGenerator idGenerator = redisson.getIdGenerator(prefix + "_" + scope.name());
        // 二级的用例
        if (SUB_NUM.contains(scope)) {
            // 每次都尝试初始化，容量为1，只有一个线程可以初始化成功
            idGenerator.tryInit(1, LIMIT);
            return Long.parseLong(prefix.split("_")[1] + StringUtils.leftPad(String.valueOf(idGenerator.nextId()), 3, "0"));
        } else {
            // 每次都尝试初始化，容量为1，只有一个线程可以初始化成功
            idGenerator.tryInit(INIT, LIMIT);
            return idGenerator.nextId();
        }
    }
}
