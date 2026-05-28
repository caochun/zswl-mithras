import { Select, App } from '@zswl/components'
import { MatchFormat } from '@/components/Format'
import { history } from '@zswl/admin'
import { AmountColumn } from '@/components/Format'

const { optionsType } = App.getData()
const ALL_COLUMNS = [
  {
    title: '融资编号',
    fixed: 'left',
    dataIndex: 'financingCode',
    actions({ financingId, financingCode }) {
      if(financingCode.startsWith('ZR')){
        return [{ name: financingCode, to: `/financial/direct/detail/${financingId}` }]
      }
      if(financingCode.startsWith('DK')){
        return [{ name: financingCode, to: `/financial/fund/detail/${financingId}` }]
      }
    },
  },
  {
    title: '融资机构',
    dataIndex: 'financingOrg',
    width: 250,
  },
  AmountColumn({
    title: '融资金额',
    dataIndex: 'financingAmount',
    width: 250,
  }),
  {
    title: '合同编号',
    dataIndex: 'contractCode',
    width: 250,
    actions({ contractId, contractCode }) {
      return [
        {
          name: contractCode,
          to: `/contract/list/detail/${contractId}`,
        },
      ]
    },
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    width: 250,
  },
  {
    title: '项目名称',
    requiredMark: true,
    dataIndex: 'projName',
    width: 250,
  },
  {
    title: '业务类型',
    requiredMark: true,
    dataIndex: 'bizTypeName',
    width: 100,
  },
  AmountColumn({
    title: '合同金额（元）',
    width: 150,
    dataIndex: 'contractAmount',
  }),
  {
    title: '出款银行开户行',
    width: 250,
    dataIndex: 'accountBank',
  },
  {
    title: '出款银行账号',
    dataIndex: 'accountNumber',
    width: 230,
  },
  AmountColumn({
    title: '出款金额（元）',
    dataIndex: 'putoutAmount',
    width: 200,
  }),
  {
    title: '出款日期',
    dataIndex: 'putoutDate',
    width: 150,
  }
]
export default ALL_COLUMNS
