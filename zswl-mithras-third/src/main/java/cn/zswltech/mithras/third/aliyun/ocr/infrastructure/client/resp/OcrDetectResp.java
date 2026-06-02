package cn.zswltech.mithras.third.aliyun.ocr.infrastructure.client.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 阿里云ocr检测返回值
 * 只包含部分数据 其他按需使用
 *
 * @author wangchuanhao
 * @date 2022/7/20 2:15 PM
 */
@NoArgsConstructor
@Data
public class OcrDetectResp {

    private Integer code;
    private DataDTO data;

    @Data
    public static class DataDTO {
        private String prismVersion;
        private Integer prismWnum;

        /**
         * 文字信息 需要从中获取置信度
         */
        private List<PrismWordsInfoDTO> prismWordsinfo;

        /**
         * 表格信息 重点关注
         */
        private List<PrismTablesInfoDTO> prismTablesinfo;

        private String content;

        /**
         * 请求id
         */
        private String sid;
        private Integer angle;
        private Integer height;
        private Integer width;
        private Integer orgHeight;
        private Integer orgWidth;

    }

    @Data
    public static class PrismWordsInfoDTO {
        private String word;

        /**
         * 置信度
         */
        private Integer prob;

        /**
         * 表格中的单元格id 可能为空
         */
        private Integer tableCellId;

        /**
         * 表格id
         */
        private Integer tableId;

        private List<PosDTO> pos;
        private Integer direction;

    }

    @Data
    public static class PrismTablesInfoDTO {
        private Integer tableId;
        private Integer xCellSize;
        private Integer yCellSize;
        private List<CellInfosDTO> cellInfos;

    }

    @Data
    public static class CellInfosDTO {
        private Integer tableCellId;
        private String word;

        /**
         * 启始x
         */
        private Integer xsc;

        /**
         * 结束x
         */
        private Integer xec;

        /**
         * 启始y
         */
        private Integer ysc;

        /**
         * 结束y
         */
        private Integer yec;

        /**
         * 具体坐标
         */
        private List<PosDTO> pos;

    }

    @Data
    public static class PosDTO {
        private Integer x;
        private Integer y;
    }
}
