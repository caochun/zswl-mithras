package cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp;

import lombok.Data;

import java.util.List;

/**
 * 股东信息
 * http://open.tianyancha.com/open/821
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:13 PM
 */
@Data
public class TycHolderResp extends TycListBaseResp<TycHolderResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 公司id
         */
        private Long cgid;

        /**
         *
         */
        private List<CapitalDTO> capital;

        /**
         * 股东名
         */
        private String name;

        /**
         *
         */
        private List<CapitalDTO> capitalActl;

        /**
         * logo
         */
        private String logo;

        /**
         * 简称
         */
        private String alias;

        /**
         * 对应表id
         */
        private Long id;

        /**
         * 股东类型 1-公司 2-⼈ 3-其它
         */
        private Integer type;

        /**
         * ⼈员hcgid
         */
        private String hcgid;

    }

    @Data
    public static class CapitalDTO {

        /**
         * 出资⾦额
         */
        private String amomon;

        /**
         * 认缴⽅式
         */
        private String paymet;

        /**
         * 出资时间
         */
        private String time;

        /**
         * 占⽐
         */
        private String percent;

    }

}
