package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 流动性风险-图表-折线图 柱状图查询-返回体体
 * @author: jackerhe
 * @date: 2023/5/16 10:03 上午
 **/
@Data
@ApiModel("流动性风险-图表-折线图 柱状图查询-返回体体")
public class ChartQueryRSP {

    //图表名称
    private String dataType;

    //图表类型 柱状图 ：bar， 折线图 line
    private String chartType;

    private List<ChartQueryDetail> details;

    /**
     * 图表各节点数值，可与前端约定扩展 悬停值，节点样式等
     **/
    @Data
    public static class ChartQueryDetail {
        //横轴
        private String name;
        //纵轴
        private Long value;
    }

}
