package cn.zswltech.mithras.service.service.message.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.core.hook.WebSocketHook;
import cn.zswltech.gruul.biz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName WebSocketHookImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/9/20 3:34 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class WebSocketHookMithrasImpl implements WebSocketHook {

    @Resource
    private UserService userService;

    @Override
    public String onOpen(String userId) {
        String realPhone;
        try {
            realPhone = userService.getRealPhone(Long.valueOf(userId));
            if (ObjectUtil.isNotEmpty(realPhone)) {
                userId = realPhone;
            }
            return userId;
        } catch (Exception e) {
            log.info("WebSocketHookImpl onOpen error userId:{}", userId, e);
            //不论处理结果如何，一定返回，不影响后续流程
            return userId;
        }
    }
}
