package cn.zswltech.mithras.service.gendoc.context;

import lombok.Data;

/**
 * 支付通知书渲染
 *
 * @author wangchuanhao
 * @date 2022/11/17 5:26 PM
 */
@Data
public class PaymentNoticeContext {

    /**
     * 收款主表id
     */
    private Long collectionBaseInfoId;

    /**
     * 备注
     */
    private String comment;

}
