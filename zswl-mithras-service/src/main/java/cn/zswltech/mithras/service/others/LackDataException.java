package cn.zswltech.mithras.service.others;

/**
 * 缺少数据
 *
 * @author wangchuanhao
 * @date 2022/6/30 5:43 PM
 */
public class LackDataException extends RuntimeException {

    public LackDataException(String msg) {
        super(msg);
    }

    public static void throwExp(String msg) {
        throw new LackDataException(msg);
    }

}
