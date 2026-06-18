import { formateCard, hasValue } from '@/utils'
import {
  genderTypeList,
  relationshipTypeList,
  certTypeList,
  shareholderTypeList,
  continuousStatusList,
} from '../../general'
const addressObj = {
  WORK_ADDRESS: '办公地址',
  REGISTRY_ADDRESS: '注册地址',
}
const diffNode = (obj, type) => {
  if (type == 'addressType') {
    return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{addressObj[obj.value]}</span>
  }
  if (type == 'Boolean') {
    return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj.value ? '是' : '否'}</span>
  }
  if (type == 'gender') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{genderTypeList[obj.value]}</span>
    )
  }
  if (type == 'certType') {
    return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{certTypeList[obj.value]}</span>
  }
  if (type == 'card') {
    return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{formateCard(obj.value)}</span>
  }
  if (type == 'shareholdingRatio') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
        {hasValue(obj.value) ? obj.value / 10000 : '-'}
      </span>
    )
  }
  if (type == 'registerCapital') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
        {hasValue(obj.value) ? obj.value / 10000 : '-'}
      </span>
    )
  }
  if (type == 'continuousStatus') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
        {continuousStatusList[obj.value]}
      </span>
    )
  }

  if (type == 'shareholderType') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
        {shareholderTypeList[obj.value]}
      </span>
    )
  }
  if (type == 'relationship') {
    return (
      <span style={{ color: obj?.isChange ? 'red' : '#333' }}>
        {relationshipTypeList[obj.value]}
      </span>
    )
  }
  // if (
  //   type == 'issueTotal' ||
  //   type == 'stockScale' ||
  //   type == 'maturityScale' ||
  //   type == 'paidTotal' ||
  //   type == 'registerCapital' ||
  //   type == 'investAmount'
  // ) {
  //   return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj.value / 10000}</span>
  // }
  return <span style={{ color: obj?.isChange ? 'red' : '#333' }}>{obj.value}</span>
}

const addressColums = [
  {
    title: '地址类型',
    dataIndex: 'addressType',
    render: (item, n, index) => {
      return diffNode(item, 'addressType')
    },
  },
  {
    title: '国家/地区',
    dataIndex: 'countryName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '省份',
    dataIndex: 'provinceName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '市',
    dataIndex: 'cityName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '区/县',
    dataIndex: 'districtName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '详细地址',
    dataIndex: 'detail',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '行政区划代码',
    dataIndex: 'regionCode',
    render: (item, index) => {
      return diffNode(item)
    },
  },
]

const linkManInfoColums = [
  {
    title: '是否主联系人',
    dataIndex: 'main',
    render: (item, n, index) => {
      return diffNode(item, 'Boolean')
    },
  },
  {
    title: '职务',
    dataIndex: 'position',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '姓名',
    dataIndex: 'name',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '性别',
    dataIndex: 'gender',
    render: (item, index) => {
      return diffNode(item, 'gender')
    },
  },
  {
    title: '电话',
    dataIndex: 'telephone',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '邮箱',
    dataIndex: 'mail',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '证件类型',
    dataIndex: 'certType',
    render: (item, index) => {
      return diffNode(item, 'certType')
    },
  },
  {
    title: '证件号码',
    dataIndex: 'certNumber',
    render: (item, index) => {
      return diffNode(item)
    },
  },
]
const bondRatingColums = [
  {
    title: '评级时间',
    dataIndex: 'rateDate',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '评级公司',
    dataIndex: 'rateCompany',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '评级',
    dataIndex: 'rate',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '评级展望',
    dataIndex: 'rateFuture',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '发行总额(亿元)',
    dataIndex: 'issueTotal',
    render: (item, index) => {
      return diffNode(item, 'issueTotal')
    },
  },
  {
    title: '发行数量（只）',
    dataIndex: 'issueAmount',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '存量规模(亿元)',
    dataIndex: 'stockScale',
    render: (item, index) => {
      return diffNode(item, 'stockScale')
    },
  },
  {
    title: '存量只数',
    dataIndex: 'stockAmount',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '到期规模(亿元)',
    dataIndex: 'maturityScale',
    render: (item, index) => {
      return diffNode(item, 'maturityScale')
    },
  },
  {
    title: '到期只数',
    dataIndex: 'maturityAmount',
    render: (item, index) => {
      return diffNode(item)
    },
  },
]

const shareholderColums = [
  {
    title: '股东类型',
    dataIndex: 'shareholderType',
    render: (item, index) => {
      return diffNode(item, 'shareholderType')
    },
  },
  {
    title: '股东名称',
    dataIndex: 'shareholderName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '实缴金额(万)',
    dataIndex: 'paidTotal',
    render: (item, index) => {
      return diffNode(item, 'paidTotal')
    },
  },
  {
    title: '出资方式',
    dataIndex: 'capitalWay',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '出资占比',
    dataIndex: 'capitalPercent',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '是否实际控制人',
    dataIndex: 'realController',
    render: (item, index) => {
      return diffNode(item, 'Boolean')
    },
  },
]
const affiliatedColums = [
  {
    title: '关联企业名称',
    dataIndex: 'enterpriseName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '关联关系',
    dataIndex: 'relationship',
    render: (item, index) => {
      return diffNode(item, 'relationship')
    },
  },
  {
    title: '注册资本(万元)',
    dataIndex: 'registerCapital',
    render: (item, index) => {
      return diffNode(item, 'registerCapital')
    },
  },
  {
    title: '存续状态',
    dataIndex: 'continuousStatus',
    render: (item, index) => {
      return diffNode(item, 'continuousStatus')
    },
  },
  {
    title: '持股比例(%)',
    dataIndex: 'shareholdingRatio',
    render: (item, index) => {
      return diffNode(item, 'shareholdingRatio')
    },
  },
  {
    title: '投资金额(万元)',
    dataIndex: 'investAmount',
    render: (item, index) => {
      return diffNode(item, 'investAmount')
    },
  },
]

const bankAccountColums = [
  {
    title: '是否主账号',
    dataIndex: 'mainAccount',
    render: (item, index) => {
      return diffNode(item, 'Boolean')
    },
  },
  {
    title: '银行账户',
    dataIndex: 'accountNumber',
    render: (item, index) => {
      return diffNode(item, 'card')
    },
  },
  {
    title: '账户名称',
    dataIndex: 'accountName',
    render: (item, index) => {
      return diffNode(item)
    },
  },
  {
    title: '开户行',
    dataIndex: 'accountBank',
    render: (item, index) => {
      return diffNode(item)
    },
  },
]
export default {
  addressColums,
  linkManInfoColums,
  shareholderColums,
  bondRatingColums,
  affiliatedColums,
  bankAccountColums,
}
