package cn.zswltech.mithras.service.service.process.prepare;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.process.prepare.RentCollectionMonthModifyAccountREQ;
import cn.zswltech.mithras.service.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.mapper.process.prepare.RentCollectionMonthDetailMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luyi
 */
@Slf4j
@Service
public class RentCollectionMonthDetailService extends ServiceImpl<RentCollectionMonthDetailMapper, RentCollectionMonthDetail> {
    public List<RentCollectionMonthDetail> byPrepareId(Long prepareId) {

        return this.list(Wrappers.<RentCollectionMonthDetail>lambdaQuery()
                .eq(RentCollectionMonthDetail::getPrepareId, prepareId)
        );

    }

    public void modifyBankInfo(RentCollectionMonthModifyAccountREQ req) {
        RentCollectionMonthDetail detail = RentCollectionMonthDetail.builder()
                .id(req.getId())
                .bankAccountName(req.getBankAccountName())
                .bankAccountNumber(req.getBankAccountNumber())
                .bankName(req.getBankName())
                .build();
        this.updateById(detail);
    }

    public List<Long> queryByIdList(List<Long> collectionIdList, LocalDateTime nextMonthLastMoment) {
        if(CollectionUtil.isEmpty(collectionIdList) || nextMonthLastMoment == null){
            return null;
        }
        List<RentCollectionMonthDetail> rentCollectionMonthDetails = this.list(Wrappers.<RentCollectionMonthDetail>lambdaQuery()
                .eq(RentCollectionMonthDetail::getYear, nextMonthLastMoment.getYear())
                .eq(RentCollectionMonthDetail::getMonth, nextMonthLastMoment.getMonth())
                .in(RentCollectionMonthDetail::getCollectionId, collectionIdList)
        );

        return rentCollectionMonthDetails.stream().map(RentCollectionMonthDetail::getCollectionId).collect(Collectors.toList());
    }
}
