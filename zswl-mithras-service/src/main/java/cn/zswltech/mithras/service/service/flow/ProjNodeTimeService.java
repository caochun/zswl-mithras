package cn.zswltech.mithras.service.service.flow;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.mapper.flow.ProjNodeTimeMapper;
import cn.zswltech.mithras.service.mapper.flow.model.ProjNodeTime;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author luyi
 */
@Service
public class ProjNodeTimeService extends ServiceImpl<ProjNodeTimeMapper, ProjNodeTime> {


    public void saveKey(Long establishId, Integer establishType, Long reviewId, String key, LocalDateTime at, boolean override) {
        ProjNodeTime one = this.getOne(Wrappers.<ProjNodeTime>lambdaQuery()
                .eq(ProjNodeTime::getEstablishId, establishId)
                .eq(ProjNodeTime::getEstablishType, establishType));
        if (null == one) {
            one = new ProjNodeTime();
            one.setReviewId(reviewId);
            one.setEstablishId(establishId);
            one.setEstablishType(establishType);
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
            one.setReviewId(reviewId);
            one.setTimeJson(jo.toString());
            updateById(one);
        }

    }
}
