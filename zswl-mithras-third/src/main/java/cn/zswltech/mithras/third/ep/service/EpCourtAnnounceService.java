package cn.zswltech.mithras.third.ep.service;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCourtAnnounceRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.third.ep.persistence.mapper.EpCourtAnnounceMapper;
import cn.zswltech.mithras.third.ep.persistence.model.EpCourtAnnounce;
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
 * @author 张欣
 */
@Slf4j
@Service
public class EpCourtAnnounceService extends ServiceImpl<EpCourtAnnounceMapper, EpCourtAnnounce> {


    @Resource
    private EpCourtAnnounceMapper epCourtAnnounceMapper;

    /***
     * 新增法院公告
     * @param epCourtAnnounce
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(EpCourtAnnounce epCourtAnnounce) {
        this.save(epCourtAnnounce);
    }

    public R<EpCourtAnnounceRSP> getEpCourtAnnounce(Long id) {
        QueryWrapper<EpCourtAnnounce> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id",id);
        EpCourtAnnounce epCourtAnnounce = epCourtAnnounceMapper.selectOne(wrapper);
        if (epCourtAnnounce != null){
            EpCourtAnnounceRSP epCourtAnnounceRSP = BeanCopyUtils.generatorObject(epCourtAnnounce, EpCourtAnnounceRSP.class);
            epCourtAnnounceRSP.setPublDate(dateConvertLocalDateTime(epCourtAnnounce.getPublDate()));
            epCourtAnnounceRSP.setInsertTime(dateConvertLocalDateTime(epCourtAnnounce.getInsertTime()));
            epCourtAnnounceRSP.setUpdateTime(dateConvertLocalDateTime(epCourtAnnounce.getMsgUpdateTime()));
            return R.ok(epCourtAnnounceRSP);
        }
        return null;
    }

    /***
     * 校验法院公告是否已经存在
     * @param msgId
     * @param jsid
     * @return
     */
    public boolean checkExistsByJsIdAndMsgId(String msgId, Integer jsid) {
        QueryWrapper<EpCourtAnnounce> wrapper = new QueryWrapper<>();
        wrapper.eq("jsid",jsid);
        wrapper.eq("msg_id",msgId);
        return epCourtAnnounceMapper.selectCount(wrapper)>0;
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
