package cn.zswltech.mithras.third.financialshare.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * @ClassName CQBusinessTypeENUM
 * @Description 应收单-业务类型
 * @Author jackerhe
 * @Date 2024/5/16 11:35 上午
 * @Version 1.0
 **/
@Getter
public enum CQCostDescribeENUM {
    SCP("应付利息_应付短期融资券利息"),
    ABS("应付利息_应付资产支持证券利息"),
    PRIVATE_BOND("应付利息_应付公司债券利息"),
    MTN("应付利息_应付中期票据利息"),
    LOAN_LONG("应付利息_应付长期借款利息_银行借款"),
    LOAN_SHORT("应付利点_应付短期借款利息_外部"),
    OTHER("长期应付款_其他");

    private final String display;

    CQCostDescribeENUM(String display) {
        this.display = display;
    }

    public static CQCostDescribeENUM getCqBusinessType(String leaseType, String originType, String timeLimitType){
       if("DK".equalsIgnoreCase(leaseType)) {
           //间融
           if(ObjectUtil.equals("BANK", originType)) {
               if(ObjectUtil.equals(timeLimitType, "LONG_TERM_LOAN")) {
                   return LOAN_LONG;
               } else
                   return LOAN_SHORT;
           } else {
               return OTHER;
           }
       } else {
           if(ObjectUtil.equals("SCP", originType)) {
               return SCP;
           } else if(ObjectUtil.equals("ABS", originType)) {
               return ABS;
           }else if(ObjectUtil.equals("PRIVATE_BOND", originType)) {
               return PRIVATE_BOND;
           }else if(ObjectUtil.equals("MTN", originType)) {
               return MTN;
           } else {
               return OTHER;
           }
       }
    }

}
