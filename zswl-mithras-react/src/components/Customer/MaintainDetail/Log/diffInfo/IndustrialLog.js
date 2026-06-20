import { Descriptions, Tooltip } from 'antd'
import {
  clientTypeList,
  genderTypeList,
  certTypeList,
  economyTypeList,
  orgTypeList,
  currencyTypeList,
  continuousStatusList,
  orgScaleTypeList,
  domesticOrAbroadObj,
} from '../../general'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import styles from './index.less'

const compareNode = (item, type) => {
  if (typeof item == 'boolean') {
    return <span>{item ? '是' : '否'}</span>
  }
  if (type == 'isRelated' || type == 'isMarket') {
    return <span>{item == '1' ? '是' : '否'}</span>
  }
  if (type == 'clientType') {
    return <span>{clientTypeList[item]}</span>
  }
  if (type == 'registerCurrencyType' || type == 'realCurrencyType') {
    return <span>{currencyTypeList[item]}</span>
  }
  if (type == 'corpGender') {
    return <span>{genderTypeList[item]}</span>
  }
  if (type == 'economyType') {
    return <span>{economyTypeList[item]}</span>
  }
  if (type == 'corpCertType') {
    return <span>{certTypeList[item]}</span>
  }
  if (type == 'continuousStatus') {
    return <span>{continuousStatusList[item]}</span>
  }
  if (type == 'orgType') {
    return <span>{orgTypeList[item]}</span>
  }
  if (type == 'orgScale') {
    return <span>{orgScaleTypeList[item]}</span>
  }
  if (type == 'groupFlag') {
    return <span>{['否', '是'][item]}</span>
  }
  // if (type == 'registerCapital' || type == 'realCapital') {
  //   return <span>{orgScaleTypeList[item] / 10000}</span>
  // }
  if (type === 'domesticOrAbroad') {
    return <span>{domesticOrAbroadObj[item]}</span>
  }
  if (type == 'enterpriseNature') {
    return <span>{getKeyOptionsLabelMapPlus('enterpriseNatureEnum')[item] || '-'}</span>
  }
  if (type == 'ownershipType') {
    return <span>{getKeyOptionsLabelMapPlus('ownershipTypeEnum')[item] || '-'}</span>
  }
  if (type == 'riskControlIndustryClassify') {
    return <span>{getKeyOptionsLabelMapPlus('riskControlIndustryClassify')[item] || '-'}</span>
  }
  if (type == 'bizScope') {
    return (
      <Tooltip title={item}>
        <div
          className={styles.yewufanwei}
          style={{
            color: item && item.isChange ? 'red' : '#333',
            minHeight: '98px',
            maxHeight: '440px',
            overflowY: 'auto',
            // width: '200px',
            // overflow: 'hidden',
            // textOverflow: 'ellipsis',
            // whiteSpace: 'nowrap',
          }}
        >
          {item}
        </div>
      </Tooltip>
    )
  }
  return <span>{item}</span>
}

const compareNodeAfter = (item, type) => {
  if (typeof item?.value == 'boolean') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {item.value ? '是' : '否'}
      </span>
    )
  }
  if (type == 'isRelated') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {item.value == '1' ? '是' : '否'}
      </span>
    )
  }
  if (type == 'clientType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {clientTypeList[item.value]}
      </span>
    )
  }
  if (type == 'registerCurrencyType' || type == 'realCurrencyType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {currencyTypeList[item.value]}
      </span>
    )
  }
  if (type == 'corpGender') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {genderTypeList[item.value]}
      </span>
    )
  }
  if (type === 'domesticOrAbroad') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {domesticOrAbroadObj[item.value]}
      </span>
    )
  }
  if (type == 'economyType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {economyTypeList[item.value]}
      </span>
    )
  }
  if (type == 'corpCertType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {certTypeList[item.value]}
      </span>
    )
  }
  if (type == 'continuousStatus') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {continuousStatusList[item.value]}
      </span>
    )
  }
  if (type == 'orgType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {orgTypeList[item.value]}
      </span>
    )
  }
  if (type == 'orgScale') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {orgScaleTypeList[item.value]}
      </span>
    )
  }
  if (type == 'groupFlag') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        <span>{['否', '是'][item?.value]}</span>
      </span>
    )
  }
  if (type == 'enterpriseNature') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {getKeyOptionsLabelMapPlus('enterpriseNatureEnum')[item?.value] || '-'}
      </span>
    )
  }

  if (type == 'ownershipType') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {getKeyOptionsLabelMapPlus('ownershipTypeEnum')[item?.value] || '-'}
      </span>
    )
  }

  if (type == 'riskControlIndustryClassify') {
    return (
      <span style={{ color: item && item.isChange ? 'red' : '#333' }}>
        {getKeyOptionsLabelMapPlus('riskControlIndustryClassify')[item?.value] || '-'}
      </span>
    )
  }
  // if (type == 'groupFlag') {
  //   return <span>{['否', '是'][item]}</span>
  // }
  // if (type == 'registerCapital' || type == 'realCapital') {
  //   return (
  //     <span style={{ color: item && item.isChange ? 'red' : '#333' }}>{item.value / 10000}</span>
  //   )
  // }
  if (type == 'bizScope') {
    return (
      <Tooltip title={item && item.value}>
        <div
          className={styles.yewufanwei}
          style={{
            color: item && item.isChange ? 'red' : '#333',
            minHeight: '98px',
            maxHeight: '440px',
            overflowY: 'auto',
            // width: '200px',
            // overflow: 'hidden',
            // textOverflow: 'ellipsis',
            // whiteSpace: 'nowrap',
          }}
        >
          {item && item.value}
        </div>
      </Tooltip>
    )
  }
  return <span style={{ color: item && item.isChange ? 'red' : '#333' }}>{item && item.value}</span>
}

const isOldOrNew = (v, s, t) => {
  if (s == 'OLD') {
    return compareNode(v, t)
  } else {
    return compareNodeAfter(v, t)
  }
}

function index(props) {
  const { detailData, flag } = props
  const isDomestic = detailData.domesticOrAbroad === 'DOMESTIC'
  return (
    <Descriptions bordered column={2} labelStyle={{ background: '#F5F6FA' }}>
      <Descriptions.Item label="客户名称">
        {isOldOrNew(detailData.clientName, flag, 'clientName')}
      </Descriptions.Item>
      <Descriptions.Item label="客户分类">
        {isOldOrNew(detailData.clientType, flag, 'clientType')}
      </Descriptions.Item>
      <Descriptions.Item label="机构分类">
        {isOldOrNew(detailData.domesticOrAbroad, flag, 'domesticOrAbroad')}
      </Descriptions.Item>
      <Descriptions.Item label="客户编号">
        {isOldOrNew(detailData.clientCode, flag, 'clientCode')}
      </Descriptions.Item>
      {/* <Descriptions.Item label="三证合一">
        {isOldOrNew(detailData.tripleCertInOne, flag, 'tripleCertInOne')}
      </Descriptions.Item> */}
      <Descriptions.Item label="中征码">
        {isOldOrNew(detailData.zhongZhengCode, flag, 'zhongZhengCode')}
      </Descriptions.Item>
      {isDomestic ? (
        <Descriptions.Item label="统一社会信用代码">
          {isOldOrNew(detailData.uscCode, flag, 'uscCode')}
        </Descriptions.Item>
      ) : (
        <Descriptions.Item label="特殊机构代码">
          {isOldOrNew(detailData.specialOrgCode, flag, 'specialOrgCode')}
        </Descriptions.Item>
      )}
      {/* <Descriptions.Item label="组织机构代码">
        {isOldOrNew(detailData.orgCode, flag, 'orgCode')}
      </Descriptions.Item> */}
      {/* <Descriptions.Item label="营业执照号">
        {isOldOrNew(detailData.bizLicenseCode, flag, 'bizLicenseCode')}
      </Descriptions.Item> */}
      <Descriptions.Item label="成立日期">
        {isOldOrNew(detailData.establishDate, flag, 'establishDate')}
      </Descriptions.Item>
      <Descriptions.Item label="核准日期">
        {isOldOrNew(detailData.approvalDate, flag, 'approvalDate')}
      </Descriptions.Item>
      <Descriptions.Item label="营业许可证到期日">
        {isOldOrNew(detailData.bizLicenseEndDate, flag, 'bizLicenseEndDate')}
      </Descriptions.Item>
      <Descriptions.Item label="存续状态">
        {isOldOrNew(detailData.continuousStatus, flag, 'continuousStatus')}
      </Descriptions.Item>
      <Descriptions.Item label="国标行业分类">
        {isOldOrNew(detailData.industryTypeName, flag, 'industryTypeName')}
      </Descriptions.Item>
      <Descriptions.Item label="风控行业分类">
        {isOldOrNew(detailData.riskControlIndustryClassify, flag, 'riskControlIndustryClassify')}
      </Descriptions.Item>
      <Descriptions.Item label="经济类型">
        {isOldOrNew(detailData.economyType, flag, 'economyType')}
      </Descriptions.Item>
      <Descriptions.Item label="组织机构类型">
        {isOldOrNew(detailData.orgType, flag, 'orgType')}
      </Descriptions.Item>
      <Descriptions.Item label="企业规模">
        {isOldOrNew(detailData.orgScale, flag, 'orgScale')}
      </Descriptions.Item>
      <Descriptions.Item label="注册资本币种">
        {isOldOrNew(detailData.registerCurrencyType, flag, 'registerCurrencyType')}
      </Descriptions.Item>
      <Descriptions.Item label="注册资本">
        {isOldOrNew(detailData.registerCapital, flag, 'registerCapital')}
      </Descriptions.Item>
      <Descriptions.Item label="实收资本币种">
        {isOldOrNew(detailData.realCurrencyType, flag, 'realCurrencyType')}
      </Descriptions.Item>
      <Descriptions.Item label="实收资本">
        {isOldOrNew(detailData.realCapital, flag, 'realCapital')}
      </Descriptions.Item>
      <Descriptions.Item label="法人性别">
        {isOldOrNew(detailData.corpGender, flag, 'corpGender')}
      </Descriptions.Item>
      <Descriptions.Item label="法人代表">
        {isOldOrNew(detailData.corpRepresent, flag, 'corpRepresent')}
      </Descriptions.Item>

      <Descriptions.Item label="法人证件类型">
        {isOldOrNew(detailData.corpCertType, flag, 'corpCertType')}
      </Descriptions.Item>
      <Descriptions.Item label="法人证件号码">
        {isOldOrNew(detailData.corpCertCode, flag, 'corpCertCode')}
      </Descriptions.Item>
      <Descriptions.Item label="企业性质">
        {isOldOrNew(detailData.enterpriseNature, flag, 'enterpriseNature')}
      </Descriptions.Item>
      <Descriptions.Item label="是否由上市公司控股">
        {isOldOrNew(detailData.ownershipType, flag, 'ownershipType')}
      </Descriptions.Item>
      <Descriptions.Item label="是否关联方">
        {isOldOrNew(detailData.isRelated, flag, 'isRelated')}
      </Descriptions.Item>

      <Descriptions.Item label="是否集团公司">
        {isOldOrNew(detailData.groupFlag, flag, 'groupFlag')}
      </Descriptions.Item>
      <Descriptions.Item label="所属集团">
        {isOldOrNew(detailData.belongGroupClientName, flag, 'belongGroupClientName')}
      </Descriptions.Item>
      <Descriptions.Item label="业务范围" span={2}>
        <div>{isOldOrNew(detailData.bizScope, flag, 'bizScope')}</div>
      </Descriptions.Item>
    </Descriptions>
  )
}

export default index
