import { AmountColumn, DateColumn, MatchOptionColumn } from '@/components/Format/defaultColumn'
import { App } from '@zswl/components'
import _ from 'lodash'

// 格式化常量
const initFormat = 1
const parentFormat = 1

const wrapItemProps = {
  inputConfig: {
    min: -Infinity,
  },
}
/**
 * 项目渲染函数 - 根据层级显示不同的样式
 * @param {string} text - 显示文本
 * @param {Object} record - 记录对象
 * @param {number} record.itemLevel - 项目层级
 * @returns {JSX.Element} 渲染的项目元素
 */
const ItemRender = (text, { itemLevel }) => {
  return (
    <div style={{ fontWeight: itemLevel === 0 ? 'bold' : 'normal', marginLeft: itemLevel * 20 }}>
      {text}
    </div>
  )
}

const editable = (record) => {
  if (_.isFunction(record.editable)) {
    return record.editable(record)
  }
  return (
    record.editable ?? {
      type: 'input',
    }
  )
}
const contentRender = (val, record) => {
  const editable = record.editable

  if (editable?.type === 'select') {
    const text = App.matchOption(editable.options, val).label
    return text
  }
  return val
}
// ==================== 实时报表列定义 ====================

// 基本情况统计表
export const basicSituationColumns = [
  // 序号	指标名称	填报内容
  // {
  //   title: '序号',
  //   dataIndex: 'rowNum',
  //   width: 80,
  //   align: 'center',
  //   editable: false,
  // },
  {
    title: '指标名称',
    dataIndex: 'item',
    width: 200,
    render: ItemRender,
    editable: false,
  },
  {
    title: '填报内容',
    dataIndex: 'content',
    width: 200,
    editable,
    render: contentRender,
  },
]

// 股东股权信息一览表-股东股权信息
export const shareholderInfoColumns = [
  // {
  //   title: '序号',
  //   dataIndex: 'onum',
  //   width: 80,
  //   align: 'center',
  //   editable: false,
  // },
  {
    title: '股东全称',
    dataIndex: 'shahFn',
    width: 200,
    editable: true,
  },
  {
    title: '统一社会信用代码/身份证号',
    dataIndex: 'shahCertNum',
    width: 220,
    editable: true,
  },
  MatchOptionColumn({
    title: '股东性质',
    dataIndex: 'shahCharCode',
    matchOption: 'PTY00021',
    width: 120,
    editable: true,
  }),
  MatchOptionColumn({
    title: '进入方式',
    dataIndex: 'shahGtoMode',
    matchOption: 'associationShahGtoModeEnum',
    width: 120,
    editable: true,
  }),
  AmountColumn({
    title: '变更前出资金额',
    dataIndex: 'altrBefShahFndrAmt',
    width: 140,
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '变更前出资占比',
    dataIndex: 'altrBefFndrRati',
    width: 140,
    initFormat: parentFormat,
    editable: true,
    wrapItemProps,
  }),
  {
    title: '股权转让',
    dataIndex: 'storTranFlag',
    matchOption: 'yesOrNoNumberEnum',
    width: 120,
    editable: true,
  },
  AmountColumn({
    title: '增/减资金情况',
    dataIndex: 'iordCptlAmt',
    width: 140,
    editable: true,
    initFormat,
    wrapItemProps,
  }),
  AmountColumn({
    title: '最新出资额',
    dataIndex: 'lastFndrAmt',
    width: 120,
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '最新持股比例',
    dataIndex: 'lastHoldRati',
    width: 140,
    initFormat: parentFormat,
    editable: true,
    wrapItemProps,
  }),
  {
    title: '批复文号',
    dataIndex: 'aprvFileNum',
    width: 150,
    editable: true,
  },
  DateColumn({
    title: '批复时间',
    dataIndex: 'aprvTime',
    width: 120,
    editable: true,
  }),
]

// 股东股权信息一览表-股东变更记录
export const shareholderChangeColumns = [
  // {
  //   title: '序号',
  //   dataIndex: 'onum',
  //   width: 80,
  //   align: 'center',
  //   editable: false,
  // },
  {
    title: '股东全称',
    dataIndex: 'shahFn',
    width: 200,
    editable: true,
  },
  {
    title: '统一社会信用代码/身份证号',
    dataIndex: 'shahCertNum',
    width: 220,
    editable: true,
  },
  MatchOptionColumn({
    title: '股东性质',
    dataIndex: 'shahCharCode',
    matchOption: 'PTY00021',
    width: 120,
    editable: true,
  }),
  MatchOptionColumn({
    title: '进入方式',
    dataIndex: 'shahGtoMode',
    matchOption: 'associationShahGtoModeEnum',
    width: 120,
    editable: true,
  }),
  AmountColumn({
    title: '变更前出资金额',
    dataIndex: 'altrBefShahFndrAmt',
    width: 140,
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '变更前出资占比',
    dataIndex: 'altrBefFndrRati',
    width: 140,
    initFormat: parentFormat,
    editable: true,
    wrapItemProps,
  }),
  MatchOptionColumn({
    title: '股权转让',
    dataIndex: 'storTranFlag',
    matchOption: 'yesOrNoNumberEnum',
    width: 120,
    editable: true,
  }),
  AmountColumn({
    title: '增/减资情况',
    dataIndex: 'iordCptlAmt',
    width: 120,
    editable: true,
    wrapItemProps,
    initFormat,
  }),
  AmountColumn({
    title: '变更后出资额',
    dataIndex: 'altrShahFndrAmt',
    width: 140,
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '变更后持股比例',
    dataIndex: 'altrHoldRati',
    width: 140,
    initFormat: parentFormat,
    editable: true,
    wrapItemProps,
  }),
  {
    title: '批复文号',
    dataIndex: 'aprvFileNum',
    width: 150,
    editable: true,
  },
  DateColumn({
    title: '批复时间',
    dataIndex: 'aprvTime',
    width: 180,
    editable: true,
  }),
]

// 高管信息一览表
export const executiveInfoColumns = [
  // {
  //   title: '序号',
  //   dataIndex: 'onum',
  //   editable: false,
  // },
  {
    title: '姓名',
    dataIndex: 'name',
    width: 120,
    editable: true,
  },
  {
    title: '证件号码',
    dataIndex: 'certNum',
    width: 180,
    editable: true,
  },
  MatchOptionColumn({
    title: '现任职务',
    dataIndex: 'currDutyCode',
    matchOption: 'PUB00247',
    width: 120,
    editable: true,
  }),
  DateColumn({
    title: '任职时间',
    dataIndex: 'aoffTime',
    width: 120,
    editable: true,
  }),
  {
    title: '批复文号',
    dataIndex: 'aprvFileNum',
    width: 150,
    editable: true,
  },
  MatchOptionColumn({
    title: '最高学历',
    dataIndex: 'highEduCode',
    matchOption: 'DIMLS803',
    width: 120,
    editable: true,
  }),
  {
    title: '毕业院校',
    dataIndex: 'gradScho',
    width: 150,
    editable: true,
  },
  {
    title: '就读专业',
    dataIndex: 'spjt',
    width: 120,
    editable: true,
  },
  {
    title: '从事金融/经济工作时间（年）',
    dataIndex: 'haveFinlTime',
    width: 200,
    editable: true,
  },
  {
    title: '联系电话',
    dataIndex: 'contTel',
    width: 140,
    editable: true,
  },
]

// 涉法涉讼涉访信息表
export const legalInfoColumns = [
  // {
  //   title: '序号',
  //   dataIndex: 'onum',
  //   width: 80,
  //   align: 'center',
  //   editable: false,
  // },
  MatchOptionColumn({
    title: '信息类别',
    dataIndex: 'caseClasCode',
    matchOption: 'PUB00250',
    width: 120,
    editable: true,
  }),
  {
    title: '合同名称',
    dataIndex: 'agmtName',
    width: 200,
    editable: true,
  },
  {
    title: '合同编号',
    dataIndex: 'agmtNo',
    width: 150,
    editable: true,
  },
  AmountColumn({
    title: '涉及金额(元)',
    dataIndex: 'invlAmt',
    width: 140,
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  MatchOptionColumn({
    title: '是否销号',
    dataIndex: 'canbFlag',
    matchOption: 'yesOrNoNumberEnum',
    width: 120,
    editable: true,
  }),
]

// 重大事项报告表一基本信息
export const majorMattersBasicColumns = [
  {
    title: '指标名称',
    dataIndex: 'item',
    width: 200,
    render: ItemRender,
    editable: false,
  },
  {
    title: '填报内容',
    dataIndex: 'content',
    width: 200,
  },
  {
    title: '指标名称',
    dataIndex: 'item2',
    width: 200,
    render: ItemRender,
    editable: false,
  },
  {
    title: '填报内容',
    dataIndex: 'content2',
    width: 200,
  },
]

// 重大事项报告表一重大事项报告情况
export const majorMattersReportColumns = [
  {
    title: '事项名称',
    dataIndex: 'piecName',
    width: 200,
  },
  {
    title: '重大事项说明',
    dataIndex: 'imprPiecExpl',
    width: 300,
  },
]
