package cn.zswltech.mithras.service.convert.ftp;

import cn.zswltech.mithras.dto.ftp.FtpInterestDetailRsp;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author dingqi
 * @date 2023/5/20
 * @description
 */
@Mapper(componentModel = "spring")
public interface FtpInterestConvert {
    @Mapping(source = "itemText", target = "interestDateText")
    FtpInterestDetailRsp entityToFtpInterestDetailRsp(FtpInterestDetailRecord record);
}
