package cn.zswltech.mithras.service.config.timeoutkick;

/**
 * @description: 登录超时常量类
 * @author: zhaozhengkang
 * @date: 2023/7/14 11:31
 */
public interface TimeoutKickConstant {

    String REDIS_KEY_PREFIX = "accessKey:";

    String TOKEN_HEADER_KEY = "token";

    String ACCESS_KEY_NAME = "_qjt_ac_";

    String MEANINGLESS_VALUE = "Meaningless value";
}
