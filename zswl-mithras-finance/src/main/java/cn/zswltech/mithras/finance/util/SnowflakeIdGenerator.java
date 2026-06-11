package cn.zswltech.mithras.finance.util;

import java.util.Random;

public class SnowflakeIdGenerator {
    // 起始时间戳（2020-01-01）
    private final static long START_TIMESTAMP = 1577808000000L;
    
    // 各部分位数
    private final static long SEQUENCE_BIT = 12;   // 序列号位数
    private final static long MACHINE_BIT = 5;     // 机器ID位数
    private final static long DATACENTER_BIT = 5;  // 数据中心位数
    
    // 各部分最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    
    // 各部分左移位数
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    
    private long datacenterId;  // 数据中心ID
    private long machineId;     // 机器ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上一次时间戳

    public SnowflakeIdGenerator() {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("Machine ID can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        //目前机器数较少，仅仅2台，冲突概率不大
        Random random = new Random();
        this.datacenterId = 1 + random.nextInt(9);
        this.machineId = 1 + random.nextInt(9);
    }
    
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        // 如果是同一时间生成的，则进行序列号自增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号超出最大值，等待下一毫秒
            if (sequence == 0L) {
                currentTimestamp = getNextMill();
            }
        } else {
            // 时间戳改变，序列号重置
            sequence = 0L;
        }
        
        // 上次生成ID的时间戳
        lastTimestamp = currentTimestamp;
        
        // 移位并通过或运算拼到一起组成64位的ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT)
                | (datacenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | sequence;
    }
    
    private long getNextMill() {
        long mill = getCurrentTimestamp();
        while (mill <= lastTimestamp) {
            mill = getCurrentTimestamp();
        }
        return mill;
    }
    
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

/*    public static void main(String[] args) {
        Map<Long, Long> map = new HashMap<>();
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator();
        for(int i = 0; i<500000; i++) {
            long l = idGenerator.nextId();
            map.put(l, map.getOrDefault(l, 0l) +1l);
        }
    }*/
}