import { DateColumn, FiledFormat, InputColumn, MatchOptionColumn } from '@/components/Format'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'

const ALL_COLUMNS = [
  DateColumn({ dataIndex: 'invoiceIssueDate', title: '开票日期', width: 150, search: true }),
  InputColumn({ dataIndex: 'registrationPageNo', title: '文件名', width: 150, search: true }),
  InputColumn({
    dataIndex: 'invoiceNo',
    title: '发票号码',
    render: (val, { isRepeat }) => {
      const title = (
        <div style={{ color: isRepeat ? 'red' : undefined }}>
          {isRepeat ? `${val} (重复)` : val}
        </div>
      )
      return <Tooltip title={title}>{title}</Tooltip>
    },
  }),
  InputColumn({ dataIndex: 'pictureCount', title: '图片张数', width: 120, search: true }),
  InputColumn({ dataIndex: 'invoicePayerName', title: '购买方', width: 180, search: true }),
  InputColumn({ dataIndex: 'invoiceSellerName', title: '销售方', search: true, width: 180 }),
  InputColumn({ dataIndex: 'invoiceTaxRate', title: '税率', width: 80 }),
  InputColumn({ dataIndex: 'invoiceTax', title: '税额', width: 120 }),
  InputColumn({ dataIndex: 'invoicePrice', title: '金额', rename: '金额（含税）', width: 150 }),
  InputColumn({ dataIndex: 'taxNotIncluded', title: '金额（不含税）', width: 150 }),
  MatchOptionColumn({
    dataIndex: 'existStample',
    title: '是否盖章',
    matchOption: 'trueOrFalse',
    width: 80,
  }),
  InputColumn({
    dataIndex: 'note',
    title: '备注',
    width: 180,
    render: (text) => {
      const title = <div style={{ color: text ? 'red' : undefined }}>{text ?? '-'}</div>
      return <Tooltip title={title}>{title}</Tooltip>
    },
  }),
  InputColumn({ dataIndex: 'invoiceGoods', title: '内容', width: 200 }),
  InputColumn({ dataIndex: 'invoicePlateSpecific', title: '型号规格', width: 200 }),
  InputColumn({ dataIndex: 'invoiceElectransUnit', title: '单位', width: 80 }),
  InputColumn({ dataIndex: 'invoiceElectransQuantity', title: '数量', width: 80 }),
  MatchOptionColumn({ dataIndex: 'invoicePrice', title: '单价', width: 80 }),
  MatchOptionColumn({
    dataIndex: 'verifyResult',
    title: '验真结果',
    matchOption: 'leaseFileOCRStatus',
    render: (text) => {
      const isPass = ['VERIFICATION_NOT_PASSED'].includes(text)
      return (
        <div style={{ color: isPass ? 'red' : undefined }}>
          {App.matchOption('leaseFileOCRStatus', text).label ?? '-'}
        </div>
      )
    },
  }),

  InputColumn({ dataIndex: 'vehicleRegistrationNumber', title: '车牌号', search: true }),
  InputColumn({ dataIndex: 'vehicleRegistrationOwner', title: '机动车所有人', search: true }),
  InputColumn({ dataIndex: 'vehicleInvoiceCarVin', title: '车架号', search: true, width: 180 }),
  InputColumn({ dataIndex: 'vehicleManufacturer', title: '制造商', search: true }),

  MatchOptionColumn({
    dataIndex: 'status',
    title: '发票状态',
    matchOption: 'leaseVatInvoiceStatus',
    search: true,
  }),
  InputColumn({ dataIndex: 'fileName', title: '原始文件名', width: 150 }),
]
export default ALL_COLUMNS
