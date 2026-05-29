package cn.zswltech.mithras.common.util;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dingqi
 * @date 2023/6/9
 * @description
 */
public class ThreadPoolUtil {
    private static final ThreadPoolExecutor commonPool = new ThreadPoolExecutor(5, 10, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100), new NamedThreadFactory("MithrasCommonThread-", false));

    public static ThreadPoolExecutor getCommonPool() {
        return commonPool;
    }
}
