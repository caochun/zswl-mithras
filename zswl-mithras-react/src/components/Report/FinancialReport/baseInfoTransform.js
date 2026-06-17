import financialReportApi from '@/api/financialReport/financialReportApi'
import { DatePicker } from 'antd'

const columns = [
  {
    title: '统一社会信用代码',
    dataIndex: 'unifSociCredCode',
    itemLevel: 0,
    editable: false,
  },
  { title: '法定代表人', dataIndex: 'legr', itemLevel: 0 },
  {
    title: '成立时间(YYYY-MM-DD)',
    dataIndex: 'setpDate',
    itemLevel: 0,
    editable: (record) => ({
      element: <DatePicker />,
      initialValue: moment(record.setpDate),
    }),
  },
  { title: '批准单位', dataIndex: 'aprvUnit', itemLevel: 0 },
  { title: '批准文号', dataIndex: 'aprvFileNum', itemLevel: 0 },
  { title: '注册资本（万元）', dataIndex: 'operCptl', itemLevel: 0 },
  { title: '其中：国有资本（万元）', dataIndex: 'sttoCptl', itemLevel: 1 },
  { title: '实收资本（万元）', dataIndex: 'paidCptl', itemLevel: 2 },
  {
    title: '经济成分（请在下拉框内选择）',
    dataIndex: 'econClasCode',
    itemLevel: 0,
    editable: {
      type: 'select',
      options: 'PTY00003',
    },
  },
  {
    title: '其中：是否中央企业控股（请在下拉框内选择）',
    dataIndex: 'ctarCorpHoldFlag',
    itemLevel: 1,
    editable: {
      type: 'select',
      options: 'yesOrNoNumberEnum',
    },
  },
  {
    title: '是否地方国企控股（请在下拉框内选择）',
    dataIndex: 'lcalSoeHoldFlag',
    itemLevel: 2,
    editable: {
      type: 'select',
      options: 'yesOrNoNumberEnum',
    },
  },
  { title: '从业人员', dataIndex: 'prtiNum', itemLevel: 0 },
  { title: '注册地址', dataIndex: 'regAddr', itemLevel: 0 },
  { title: '实际经营地址', dataIndex: 'actlOperAddr', itemLevel: 0 },
  {
    title: '内资/内资试点/外资（请在下拉框内选择）',
    dataIndex: 'corpClasCode',
    itemLevel: 0,
    editable: {
      type: 'select',
      options: 'PTY00221',
    },
  },
  {
    title: '厂商系/非厂商系（请在下拉框内选择）',
    dataIndex: 'mnfrFlag',
    itemLevel: 0,
    editable: {
      type: 'select',
      options: 'associationMnfrFlagEnum',
    },
  },
  {
    title: '上市/非上市（请在下拉框内选择）',
    dataIndex: 'listFlag',
    itemLevel: 0,
    editable: {
      type: 'select',
      options: 'associationListFlagEnum',
    },
  },
  { title: '分支机构数量（家）', dataIndex: 'brchInsNum', itemLevel: 0 },
  { title: '其中：省内（家）', dataIndex: 'wprvBrchInsNum', itemLevel: 1 },
  { title: '省外（家）', dataIndex: 'oprvBrchInsNum', itemLevel: 2 },
  { title: '设立的其他融资租赁子公司数量', dataIndex: 'fnlChilCorpNum', itemLevel: 0 },
  { title: '设立的特殊项目公司（SPV)数量', dataIndex: 'spclProjCorpSpvVol', itemLevel: 0 },
  { title: '分支机构所在地', dataIndex: 'brchInsAddr', itemLevel: 0 },
  { title: '经批准的业务范围', dataIndex: 'hsapBusiScop', itemLevel: 0 },
  { title: '实控人', dataIndex: 'actlCtlr', itemLevel: 0 },
  { title: '实控人持股比例', dataIndex: 'actlCtlrHoldRati', itemLevel: 0 },
  { title: '公司联系人', dataIndex: 'corpConp', itemLevel: 0 },
  { title: '联系电话', dataIndex: 'contTel', itemLevel: 0 },
  { title: '联系邮箱', dataIndex: 'contMail', itemLevel: 0 },
  { title: '公司网址', dataIndex: 'corpWeb', itemLevel: 0 },
]
/**
 * 转换基本情况表数据的函数
 * @param {Object} data - 基本情况数据对象
 * @returns {Array} 转换后的表格数据数组
 */
export const transformBaseInfoData = (data) => {
  if (!data) return []

  return columns.map(({ title: item, dataIndex, ...rest }, index) => ({
    item,
    content: data[dataIndex],
    rowNum: index + 1,
    id: index + 1,
    ...rest,
    parentId: data.id,
  }))
}
export const baseInfoModifyApi = async ({ reportInstanceId, dataList }) => {
  const params = {
    id: dataList[0].parentId,
    reportInstanceId,
  }
  columns.forEach((item, index) => {
    params[item.dataIndex] = dataList[index].content
  })
  return await financialReportApi.postModifyBasicSituation(params)
}

export const majorMattersBasicColumnsTransform = (data) => {
  if (!data) {
    return []
  }

  return [
    {
      id: 1,
      item: '公司名称',
      content: data.corpName || '',
      item2: '注册资本',
      content2: data.leglCptl || '',
      parentId: data.id,
    },
    {
      id: 2,
      item: '营业地址',
      content: data.busiAddr || '',
      item2: '法定代表人',
      content2: data.corpLegpName || '',
      parentId: data.id,
    },
    {
      id: 3,
      item: '分支机构数量',
      content: data.brchInsNum || '',
      item2: '董事长',
      content2: data.chrmName || '',
      parentId: data.id,
    },
    {
      id: 4,
      item: '总经理',
      content: data.gmgrName || '',
      item2: '联系方式',
      content2: data.inftContMode || '',
      parentId: data.id,
    },
  ]
}
