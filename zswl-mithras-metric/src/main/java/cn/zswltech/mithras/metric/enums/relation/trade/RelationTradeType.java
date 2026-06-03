package cn.zswltech.mithras.metric.enums.relation.trade;

/**
 * @author yibin
 */
public class RelationTradeType {
    // "投融资类";
    public static class TOU_RONG_ZI_LEI {
        public static final String DAI_KUAI = "贷款（含贸易融资）";
        public static final String RONG_ZI_ZU_LIN = "融资租赁";
        public static final String RONG_ZI_RONG_QUAN = "融资融券";
        public static final String ZAI_TI_TOU_ZI = "特定目的载体投资";
        public static final String MAI_RU_FAN_SHOU = "买入返售";
        public static final String CHENG_DUI_TIE_XIAN = "票据承兑和贴现";
        public static final String TOU_ZHI = "透支";
        public static final String CUN_DAN = "存单";
        public static final String ZHAI_QUAN_TOU_ZI = "债券投资";
        public static final String DAI_KUAN_CHENG_NUO_LEI = "贷款承诺类";
        public static final String JIN_RONG_YAN_SHENG_PIN = "金融衍生品交易";
        //        public static final String ZAI_TI_TOU_ZI = "特定目的载体投资";
        public static final String TOU_ZI_JIN_RONG = "投资金融产品";
        public static final String ZHENG_QUAN_HUI_GOU = "证券回购类";
        public static final String CHAI_JIE = "拆借";
        public static final String KAI_LI_XIN_YONG = "开立信用证";
        public static final String BAO_LI = "保理";
        public static final String DAN_BAO = "担保";
        public static final String BAO_HAN = "保函";
        public static final String DAI_KUAN_CHENG_NUO = "贷款承诺";
        public static final String QI_TA_CREDIT_RISK = "其他承担信用风险的业务";
    }

    //资产转移类
    public static class ZI_CHAN_ZHUAN_YI {
        public static final String ZI_YONG_DONG_CHAN = "自用动产与不动产买卖";
        public static final String XIN_DAI_ZI_CHAN = "信贷资产及其收（受）益权买卖";
        public static final String DI_ZHAI_ZI_CHAN = "抵债资产的接收和处置";
        public static final String QI_TA_CHU_SHOU = "其他出售资产交易";
    }

    public static class TI_GONG_FU_WU {
        public static final String ZHENG_XIN_XIN_YONG = "征信、信用评级、资产评估、法律、审计、精算、咨询顾问服务";
        public static final String RUAN_JIAN_XIN_XI = "软件和信息技术、互联网数据服务";
        public static final String FEI_JIN_RONG_JI_GOU = "非金融机构支付服务类";
        public static final String JIN_RONG_XIN_XI = "金融信息服务";
        public static final String XIN_XI_ZHAN_SHI = "信息展示、销售推介、委托或受托销售";
        public static final String YOU_JIA_ZHENG_QUAN = "有价证券交易经纪服务和承销服务";
        public static final String ZI_YONG_DONG_CHAN = "自用动产与不动产租赁、其他租赁资产交易";

    }

    public static class QI_TA_LEI_XING {
        public static final String CUN_KUAN = "存款";
        public static final String BAO_XIAN_YE_WU = "保险业务";
        public static final String QI_TA_ZHUAN_YI = "其他可能导致利益转移的事项";
    }

}
