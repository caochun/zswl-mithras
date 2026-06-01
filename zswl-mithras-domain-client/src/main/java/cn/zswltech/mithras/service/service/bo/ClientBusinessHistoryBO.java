package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.mapper.model.client.ClientBusinessHistory;
import cn.zswltech.mithras.service.service.third.model.MithrasShareholderInfo;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/5/30
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientBusinessHistoryBO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 客户名称
     */
    private String tycName;

    /**
     * 法人代表
     */
    private String tycCorpRepresent;

    /**
     * 股东信息
     */
    private List<MithrasShareholderInfo> tycShareHolderInfo;

    public ClientBusinessHistory getBo(){
        ClientBusinessHistory history = new ClientBusinessHistory();
        history.setId(id);
        history.setClientId(clientId);
        history.setTycName(tycName);
        history.setTycCorpRepresent(tycCorpRepresent);
        history.setTycShareHolderInfo(JSON.toJSONString(tycShareHolderInfo));
        return history;
    }
}
