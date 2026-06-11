package cn.zswltech.mithras.foundation.thirdparty;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求第三方平台api时，需要把POJO类——T转为请求的body
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
public interface PlatformApiHandler<T, F> {

    Logger log = LoggerFactory.getLogger(PlatformApiHandler.class);

    PlatformApiEnum platformApi();

    /**
     * 封装请求入参
     * @param reqData
     * @return
     */
    default String request(T reqData) {
        return null;
    }

    /**
     * 解析返回结果
     * @param data
     * @return
     */
    F response(String data);

    /**
     * 执行http请求
     * @param reqData
     * @return
     */
    F execute(T reqData);


    /**
     * 自动重试,
     * */
    default void failureRetry(String platform){
    }

    default boolean isFailureSave(){
        return false;
    }

    /**
     * 自动重试
     * @param reqData
     * @param retryTimes
     * @return
     */
    default F executeAutoRetry(T reqData, Integer retryTimes) {
        try {
            F resData = execute(reqData);
            if (!isExecuteSuccess(resData)) {
                log.error("三方接口调用失败, 剩余自动重试次数:{}, req:{}, res:{}",
                        retryTimes - 1, JSON.toJSONString(reqData), JSON.toJSONString(resData));
                if (retryTimes > 1) {
                    return executeAutoRetry(reqData, retryTimes - 1);
                } else {
                    return execute(reqData);
                }
            }
            return resData;
        } catch (Exception e) {
            // 抛异常肯定算失败了
            log.error("三方接口调用异常, 剩余自动重试次数:{}, req:{}", retryTimes - 1, JSON.toJSONString(reqData));
            if (retryTimes > 1) {
                return executeAutoRetry(reqData, retryTimes - 1);
            } else {
                return execute(reqData);
            }
        }
    }

    default boolean isExecuteSuccess(F resData) {
        return true;
    }
}
