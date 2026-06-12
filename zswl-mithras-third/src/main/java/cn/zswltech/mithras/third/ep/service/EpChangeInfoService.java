package cn.zswltech.mithras.third.ep.service;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpChangeInfoRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.third.ep.persistence.mapper.EpChangeInfoMapper;
import cn.zswltech.mithras.third.ep.persistence.model.EpChangeInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author ZHANGXIN
 */
@Slf4j
@Service
public class EpChangeInfoService extends ServiceImpl<EpChangeInfoMapper, EpChangeInfo> {


    @Resource
    private EpChangeInfoMapper epChangeInfoMapper;

    /***
     * 企业变更信息存储
     * @param epChangeInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(EpChangeInfo epChangeInfo) {
        this.save(epChangeInfo);
    }

    public R<EpChangeInfoRSP> getEpChangeInfo(Long id) {
        QueryWrapper<EpChangeInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id", id);
        EpChangeInfo changeInfo = epChangeInfoMapper.selectOne(wrapper);
        if (changeInfo != null) {
            EpChangeInfoRSP epChangeInfoRSP = BeanCopyUtils.generatorObject(changeInfo, EpChangeInfoRSP.class);
            epChangeInfoRSP.setChange(changeInfo.getChangeItem());
            epChangeInfoRSP.setChangeDate(dateConvertLocalDateTime(changeInfo.getChangeDate()));
            epChangeInfoRSP.setInsertTime(dateConvertLocalDateTime(changeInfo.getInsertTime()));
            epChangeInfoRSP.setUpdateTime(dateConvertLocalDateTime(changeInfo.getMsgUpdateTime()));
            return R.ok(epChangeInfoRSP);
        }
        return null;
    }

    /***
     * 校验企业变更是否已经存在
     * @param jsid
     * @param msgId
     * @return
     */
    public boolean checkExistsByJsIdAndMsgId(Long jsid, String msgId) {
        QueryWrapper<EpChangeInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("jsid", jsid);
        wrapper.eq("msg_id", msgId);
        return epChangeInfoMapper.selectCount(wrapper) > 0;
    }

    /***
     * Date转LocalDateTime
     * @param date
     * @return
     */
    public String dateConvertLocalDateTime(Date date) {
        //date为null
        if (Objects.isNull(date)) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = localDateTime.format(dateTimeFormatter);
        return format;
    }
}
