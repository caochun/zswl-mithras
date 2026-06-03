package cn.zswltech.mithras.third.financialshare.application.dto;

import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import lombok.Data;


/**
 * 苍穹2期 接口删除
 **/
@Data
public class CQ2WithdrawVO {
    /**
     * 来源途径
     * {@link ExceptionSourceENUM#name()}
     **/
    private String source;

    /**
     * {@link PlatformApiEnum#getCqNeedManualApiList()}
     **/
    private String platform;

    /**
     * 业务主建 用于寻找对应业务信息
     **/
    private String businessKey;
}
