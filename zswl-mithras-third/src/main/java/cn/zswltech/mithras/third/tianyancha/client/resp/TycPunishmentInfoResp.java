package cn.zswltech.mithras.third.tianyancha.client.resp;

import lombok.Data;


/**
 * 行政处罚
 *
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:29 PM
 */
@Data
public class TycPunishmentInfoResp extends TycListBaseResp<TycPunishmentInfoResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 处罚单位
         */
        private String departmentName;

        /**
         * 处罚事由/违法⾏为类型
         */
        private String reason;

        /**
         * 处罚依据（source=信⽤中国时返回数据）
         */
        private String evidence;

        /**
         * 处罚状态（source=信⽤中国时返回数据）
         */
        private String punishStatus;

        /**
         * 备注（source=国家市场监督管理总局时返回数据）
         */
        private String remark;

        /**
         * 数据来源
         */
        private String source;

        /**
         * 处罚类别1（source=信⽤中国时返回数据）
         */
        private String type;

        /**
         * 处罚结果/内容
         */
        private String content;

        /**
         * 处罚⽇期
         */
        private String decisionDate;

        /**
         * 法定代表⼈（source=国家市场监督管理总局时返回数据）
         */
        private String legalPersonName;

        /**
         * 处罚名称（source=信⽤中国时返回数据）
         */
        private String punishName;

        /**
         * 决定⽂书号
         */
        private String punishNumber;

        /**
         * 处罚类别2（source=信⽤中国时返回数据）
         */
        private String typeSecond;
    }


}
