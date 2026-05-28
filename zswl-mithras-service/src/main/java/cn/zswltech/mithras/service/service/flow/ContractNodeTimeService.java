package cn.zswltech.mithras.service.service.flow;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.mapper.flow.ContractNodeTimeMapper;
import cn.zswltech.mithras.service.mapper.flow.model.ContractNodeTime;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author luyi
 */
@Service
public class ContractNodeTimeService extends ServiceImpl<ContractNodeTimeMapper, ContractNodeTime> {


    public void saveKey(Long reviewId, Long contractId, String key, LocalDateTime at, boolean override) {
        ContractNodeTime one = this.getOne(Wrappers.<ContractNodeTime>lambdaQuery()
                .eq(ContractNodeTime::getContractId, contractId));
        if (null == one) {
            one = new ContractNodeTime();
            one.setReviewId(reviewId);
            one.setContractId(contractId);
            JSONObject jo = new JSONObject();
            jo.put(key, at);
            one.setTimeJson(jo.toString());
            save(one);
        } else {
            String timeJson = one.getTimeJson();
            JSONObject jo = JSONUtil.parseObj(timeJson);
            LocalDateTime time = jo.getLocalDateTime(key, null);
            if (null != time && !override) {
                return;
            }
            jo.set(key, at);
            one.setTimeJson(jo.toString());
            one.setReviewId(reviewId);
            updateById(one);
        }

    }
}
