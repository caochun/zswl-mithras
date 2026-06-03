package cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/16 22:15
 */
public enum Department {
//        /**
//         * name: 浙江业务部
//         */
//        JCSSYWB,
//
//        /**
//         * name: 高端装备业务部
//         */
//        JSSYB,
//
//        /**
//         * name: 交通物流业务部
//         */
//        JTYSYWB,
//
//        /**
//         * name: 智能制造业务部
//         */
//        XJZZHXJJTD,
//
//        /**
//         * name: 公共事业业务部
//         */
//        GGSY,
//
//        /**
//         * name: 航运业务部
//         */
//        HYYWB,
//
////        /**
////         * name: 机械和加工业务部
////         */
////        JXHJGYWB,
//
//        /**
//         * name: 绿色产业业务部
//         */
//        LSCYYWB,
//
//        /**
//         * name: 文化健康业务部
//         */
//        XNYYWB,
//
//        /**
//         * name: 工程建设业务部
//         */
//        SYCYWB,
////
////        /**
////         * name: 化工建材业务部
////         */
////        HGJCYWB,
//
////        /**
////         * name: 冷链物流团队
////         */
////        LLWLTD,

        JCSSYWB("浙江业务部"),
        JSSYB("高端装备业务部"),
        JTYSYWB("交通物流业务部"),
        XJZZHXJJTD("智能制造业务部"),
        GGSY("公共事业业务部"),
        HYYWB("航运业务部"),
        LSCYYWB("绿色产业业务部"),
        XNYYWB("文化健康业务部"),
        SYCYWB("工程建设业务部");

        private final String name;

        Department(String name) {
                this.name = name;
        }

        public String getName() {
                return name;
        }
}