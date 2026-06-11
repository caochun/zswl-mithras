package cn.zswltech.mithras.third.ep.service;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCaseInfoRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.third.ep.mapper.EpCaseInfoMapper;
import cn.zswltech.mithras.third.ep.model.EpCaseInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class EpCaseInfoService extends ServiceImpl<EpCaseInfoMapper, EpCaseInfo> {

    @Autowired
    private EpCaseInfoMapper epCaseInfoMapper;

    /**
     * 新增立案信息
     *
     * @param epCaseInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(EpCaseInfo epCaseInfo) {
        this.save(epCaseInfo);
    }

    public R<EpCaseInfoRSP> getCaseInfo(Long id) {
        QueryWrapper<EpCaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("msg_id", id);
        EpCaseInfo caseInfo = epCaseInfoMapper.selectOne(queryWrapper);
        if (caseInfo != null) {
            EpCaseInfoRSP epCaseInfoRSP = BeanCopyUtils.generatorObject(caseInfo, EpCaseInfoRSP.class);
            epCaseInfoRSP.setCaseDate(dateConvertLocalDateTime(caseInfo.getCaseDate()));
            epCaseInfoRSP.setSessionDate(dateConvertLocalDateTime(caseInfo.getSessionDate()));
            epCaseInfoRSP.setEndDate(dateConvertLocalDateTime(caseInfo.getEndDate()));
            epCaseInfoRSP.setInsertTime(dateConvertLocalDateTime(caseInfo.getInsertTime()));
            epCaseInfoRSP.setMsgUpdateTime(dateConvertLocalDateTime(caseInfo.getMsgUpdateTime()));
            return R.ok(epCaseInfoRSP);
        }
        return null;
    }
    /***
     * 校验立案信息是否存在
     * @param jsid
     * @param msgId
     * @return
     */
    public boolean checkExistsByJsIdAndMsgId(Long jsid, String msgId) {
        QueryWrapper<EpCaseInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("jsid", jsid);
        queryWrapper.eq("msg_id", msgId);
        return epCaseInfoMapper.selectCount(queryWrapper) > 0;
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
