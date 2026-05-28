import { AmountEditable } from '@/components/Format'
import {
  AmountColumn,
  DateColumn,
  InputColumn,
  MatchOptionColumn,
} from '@/components/Format/defaultColumn'

const initFormat = 1
const parentFormat = 1 / 100
const ItemRender = (text, { itemLevel }) => {
  return (
    <div style={{ fontWeight: itemLevel === 0 ? 'bold' : 'normal', marginLeft: itemLevel * 20 }}>
      {text}
    </div>
  )
}

const wrapItemProps = {
  inputConfig: {
    min: -Infinity,
  },
}

// 融资租赁公司对外融资清单（季报）
export const businessColumns = [
  AmountColumn({
    title: '借款余额',
    dataIndex: 'loanBal',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  MatchOptionColumn({
    title: '融资业务类型',
    dataIndex: 'finBusiTypeCode',
    matchOption: 'EVT00052',
    editable: true,
  }),
  InputColumn({ title: '资金提供方', dataIndex: 'cptlProv', editable: true }),
  AmountColumn({
    title: '融资利率',
    dataIndex: 'finIntr',
    initFormat: parentFormat,
    suffix: '%',
    editable: true,
    wrapItemProps,
  }),
  DateColumn({ title: '借款日期', dataIndex: 'finLoanDate', editable: true }),
  DateColumn({ title: '到期日', dataIndex: 'finMatuDate', editable: true }),
]

// 融资租赁公司服务实体经济情况（新）
export const economyColumns = [
  InputColumn({
    title: '项目',
    dataIndex: 'item',
    render: ItemRender,
    editable: false,
  }),
  AmountColumn({ title: '期初数', dataIndex: 'period', initFormat, editable: true, wrapItemProps }),
  AmountColumn({
    title: '本期发生额',
    dataIndex: 'amount',
    tooltip: '数量减少用负号表示',
    initFormat,
    editable: true,
    wrapItemProps: {
      inputConfig: {
        min: -Infinity,
      },
    },
  }),
  AmountColumn({
    title: '期末数',
    dataIndex: 'endPeriod',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
]

// 融资租赁公司关联方信息汇总表
export const relatedColumns = [
  // InputColumn({ title: '序号', dataIndex: 'onum', width: 80, editable: true }),
  InputColumn({ title: '关联方名称', dataIndex: 'relpName', editable: true }),
  MatchOptionColumn({
    title: '是否为本公司股东关联方',
    dataIndex: 'corpShahRelpFlag',
    matchOption: 'yesOrNoNumberEnum',
    width: 200,
    editable: true,
  }),
  InputColumn({ title: '本公司股东名称', dataIndex: 'corpShahName', width: 180, editable: true }),
  {
    title: '单一关联方',
    children: [
      {
        title: '表内业务',
        children: [
          AmountColumn({
            title: '关联方租赁余额',
            dataIndex: 'onblRelpLeasBalSrlp',
            initFormat,
            width: 160,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '占净资产比例',
            dataIndex: 'onblOnarSrlp',
            suffix: '%',
            initFormat: parentFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      {
        title: '表外业务',
        children: [
          AmountColumn({
            title: '担保',
            dataIndex: 'ofblGuarSrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '其他',
            dataIndex: 'ofblOthSrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      {
        title: '扣减项',
        children: [
          AmountColumn({
            title: '合格质物',
            dataIndex: 'deitQulfSbimSrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '合格保证',
            dataIndex: 'deitQulfAsueSrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '其他',
            dataIndex: 'deitOthSrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      AmountColumn({
        title: '信用风险敞口',
        dataIndex: 'credExpsSrlp',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
    ],
  },
  {
    title: '关联方所在集团',
    children: [
      InputColumn({ title: '所在集团名称', dataIndex: 'grlpName', editable: true }),
      {
        title: '表内业务',
        children: [
          AmountColumn({
            title: '关联方租赁余额',
            dataIndex: 'onblRelpLeasBalGrlp',
            initFormat,
            width: 160,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '占净资产比例',
            dataIndex: 'onblOnarGrlp',
            suffix: '%',
            initFormat: parentFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      {
        title: '表外业务',
        children: [
          AmountColumn({
            title: '担保',
            dataIndex: 'ofblGuarGrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '其他',
            dataIndex: 'ofblOthGrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      {
        title: '扣减项',
        children: [
          AmountColumn({
            title: '合格质物',
            dataIndex: 'deitQulfSbimGrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '合格保证',
            dataIndex: 'deitQulfAsueGrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
          AmountColumn({
            title: '其他',
            dataIndex: 'deitOthGrlp',
            initFormat,
            editable: true,
            wrapItemProps,
          }),
        ],
      },
      AmountColumn({
        title: '信用风险敞口',
        dataIndex: 'credExpsGrlp',
        initFormat,
        editable: true,
        wrapItemProps,
      }),
    ],
  },
]

// 融资租赁公司利润表
export const profitColumns = [
  InputColumn({ title: '项目', dataIndex: 'item', render: ItemRender, editable: false }),
  AmountColumn({
    title: '本季金额',
    dataIndex: 'mainBusiIncmActm',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '本年累计金额',
    dataIndex: 'mainBusiIncmTyag',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '上年同期金额',
    dataIndex: 'mainBusiIncmCply',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
]

// 融资租赁公司业务情况表
export const businessSituationColumns = [
  {
    title: '主要指标',
    dataIndex: 'item',
    render: ItemRender,
    editable: false,
  },
  AmountColumn({
    title: '期初数',
    dataIndex: 'totIncmAbop',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '本期发生额',
    dataIndex: 'totIncmAotc',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '期末数',
    dataIndex: 'totIncmAeop',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
]

// 融资租赁公司主要业务清单
export const mainBusinessColumns = [
  // InputColumn({ title: '序号', dataIndex: 'onum', width: 80, editable: true }),
  InputColumn({ title: '合同名称', dataIndex: 'agmtName', editable: true }),
  InputColumn({ title: '合同编号', dataIndex: 'agmtNo', editable: true }),
  DateColumn({ title: '合同签订日期', dataIndex: 'agmtSignDate', editable: true }),
  DateColumn({ title: '合同到期日', dataIndex: 'agmtMatuDate', editable: true }),
  MatchOptionColumn({
    title: '合同类型',
    dataIndex: 'agmtTypeCode',
    matchOption: 'DIMLS079',
    editable: true,
  }),
  InputColumn({ title: '租赁物类型', dataIndex: 'lasdType', editable: true }),
  MatchOptionColumn({
    title: '项目行业分类',
    dataIndex: 'projIndtClasCode',
    matchOption: 'PUB00234',
    editable: true,
  }),
  InputColumn({ title: '客户姓名', dataIndex: 'custName', editable: true }),
  InputColumn({ title: '客户证件号码', dataIndex: 'custCertNum', editable: true }),
  MatchOptionColumn({
    title: '客户规模',
    dataIndex: 'custScalCode',
    matchOption: 'PTY00019',
    editable: true,
  }),
  AmountColumn({
    title: '融资租赁投放额',
    dataIndex: 'fnlRels',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '收回本金',
    dataIndex: 'wthdPrin',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '租金余额',
    dataIndex: 'rentBal',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  AmountColumn({
    title: '综合融资成本',
    dataIndex: 'cmphFinCost',
    initFormat: parentFormat,
    suffix: '%',
    editable: true,
    wrapItemProps,
  }),
  MatchOptionColumn({
    title: '增信情况',
    dataIndex: 'udpnSituCode',
    matchOption: 'PTY00212',
    editable: true,
  }),
  InputColumn({ title: '增信方', dataIndex: 'udpn', editable: true }),
  AmountColumn({
    title: '逾期租金',
    dataIndex: 'ovduRent',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  MatchOptionColumn({
    title: '逾期天数',
    dataIndex: 'ovduDaysCode',
    matchOption: 'EVT00051',
    editable: true,
  }),
  InputColumn({ title: '逾期处置情况', dataIndex: 'ovduDspsProg', editable: true }),
  MatchOptionColumn({
    title: '是否纳入不良',
    dataIndex: 'npFlag',
    editable: true,
    matchOption: 'yesOrNoNumberEnum',
  }),
  AmountColumn({
    title: '不良余额',
    dataIndex: 'npBal',
    initFormat,
    editable: true,
    wrapItemProps,
  }),
  InputColumn({ title: '客户数量', dataIndex: 'custVol', editable: true }),
]
