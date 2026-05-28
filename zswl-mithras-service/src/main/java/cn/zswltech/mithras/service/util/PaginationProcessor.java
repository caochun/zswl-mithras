package cn.zswltech.mithras.service.util;

import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 数据分页处理器
 * @author: zhaozhengkang
 * @date: 2023/7/12 10:04
 */
@Slf4j
public abstract class PaginationProcessor<T> {
    private final int pageSize;
    private final AtomicInteger pageNum = new AtomicInteger(1);
    private final BaseMapper<T> mapper;
    private LambdaQueryWrapper<T> wrapper;
    private final ExecutorService executorService;

    public PaginationProcessor(int pageSize, Class<?> mapperClazz) {
        this.pageSize = pageSize;
        mapper = (BaseMapper<T>) SpringContextHolder.getBean(mapperClazz);
        executorService = new ThreadPoolExecutor(8, 10, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    public void setWrapper(LambdaQueryWrapper<T> wrapper) {
        this.wrapper = wrapper;
    }


    public void process() {
        Integer total = mapper.selectCount(wrapper);
        if (total == 0) {
            return;
        }
        int totalPage = total / pageSize + 1;
        CountDownLatch latch = new CountDownLatch(totalPage);
        while (pageNum.get() <= totalPage) {
            Page<T> page = mapper.selectPage(new Page<T>(pageNum.getAndIncrement(), pageSize), wrapper);
            List<T> list = page.getRecords();
            executorService.execute(() -> {
                doProcess(list);
                latch.countDown();
            });
        }
        try {
            boolean await = latch.await(1, TimeUnit.MINUTES);
            if (!await) {
                log.error("分页处理器处理超时失败，请核查");
            }
        } catch (InterruptedException e) {
            log.error("分页处理器处理被中断，请核查", e);
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void doProcess(List<T> list);
}

