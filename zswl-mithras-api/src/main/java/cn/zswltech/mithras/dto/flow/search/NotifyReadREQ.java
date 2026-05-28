package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 消息已读
 *
 * @author wangchuanhao
 * @date 2022/8/1 10:37 AM
 */
@Data
public class NotifyReadREQ {

    @ApiModelProperty("通知id列表")
    private List<Long> notifyIdList;

}
