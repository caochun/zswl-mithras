package cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp;

import lombok.Data;

import java.util.List;

/**
 * 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycDishonestResp extends TycListBaseResp<TycDishonestResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 法⼈、负责⼈姓名
         */
        private String businessentity;

        /**
         * 省份地区
         */
        private String areaname;

        /**
         * 法院
         */
        private String courtname;

        /**
         * 未履⾏部分
         */
        private String unperformPart;

        /**
         * 法定负责⼈/主要负责⼈信息
         */
        private List<StaffDTO> staff;

        /**
         * 失信⼈类型，0代表⼈，1代表公司
         */
        private String type;

        /**
         * 已履⾏部分
         */
        private String performedPart;

        /**
         * 失信⼈名称
         */
        private String iname;

        /**
         * 失信被执⾏⼈⾏为具体情形
         */
        private String disrupttypename;

        /**
         * 案号
         */
        private String casecode;

        /**
         * 身份证号码/组织机构代码
         */
        private String cardnum;

        /**
         * 履⾏情况
         */
        private String performance;

        /**
         * ⽴案时间
         */
        private Long regdate;

        /**
         * 发布时间
         */
        private Long publishdate;

        /**
         * 做出执⾏的依据单位
         */
        private String gistunit;

        /**
         * ⽣效法律⽂书确定的义务
         */
        private String duty;

        /**
         * 执⾏依据⽂号
         */
        private String gistid;
    }

    @Data
    public static class StaffDTO {

        /**
         * ⻆⾊
         */
        private String role;

        /**
         * 脱敏证件号
         */
        private String code;

        /**
         * 法⼈姓名
         */
        private String name;
    }

}
