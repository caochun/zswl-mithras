package cn.zswltech.mithras.payment.mapper;

import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @description payment_base_info
* @author zhaozhengkang
* @date 2022-08-12
*/
public interface PaymentBaseInfoMapper extends CustomBaseMapper<PaymentBaseInfo> {

    @Update({
            "<script>",
            "update payment_base_info set financial_status = 1 where payment_code in",
            "<foreach collection='codes' open='(' close=')' separator=',' item='item'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    int updateFinanceStatusByCode(@Param("codes")List<String> codes);
}
