import { useMemo } from 'react'
import { Button, Table } from '@zswl/components'
import { Space, Checkbox } from 'antd'
import { observer } from '@zswl/admin'
import IconFont from '@/components/Icon'
import Store from './store'
import styles from './index.less'
import { AmountColumn, MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'
import { JumpContractDetail, JumpProjDetail } from '../../Shared/JumpDetail'

const FinancialFundDetailProperty = ({
  financingId,
  isFormApproval,
  businessVersion,
  baseInfoData: detail,
  baseStore,
}) => {
  const store = useMemo(() => {
    return new Store({ businessVersion, isFormApproval, financingId, detail, baseStore })
  }, [businessVersion, isFormApproval, financingId, detail, baseStore])
  const columns = useMemo(() => {
    return [
      {
        title: '合同编号',
        dataIndex: 'contractCode',
        width: 250,
        render(val, t) {
          return (
            <JumpContractDetail
              title={t.contractCode}
              contractId={t.contractId}
            ></JumpContractDetail>
          )
        },
      },
      {
        title: '客户名称',
        dataIndex: 'clientName',
        width: 260,
      },
      {
        title: '项目名称',
        dataIndex: 'projName',
        width: 260,
      },
      {
        title: '业务类型',
        dataIndex: 'bizTypeName',
      },
      AmountColumn({
        title: '合同金额(元)',
        dataIndex: 'contractAmount',
        width: 150,
        align: 'right',
      }),
      {
        title: '出款银行开户行',
        width: 300,
        dataIndex: 'accountBank',
      },
      {
        title: '出款银行账号',
        dataIndex: 'accountNumber',
        width: 250,
      },
      AmountColumn({
        title: '出款金额',
        dataIndex: 'putoutAmount',
        align: 'right',
      }),
      {
        title: '出款日期',
        dataIndex: 'putoutDate',
        rules: [{ required: true }],
      },
    ]
  }, [store])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>投放资产明细</div>
      </div>
      <Table
        columnsFilter={'detail_CostDetail_1'}
        onFilter={(key,val) => saveServer('detail_CostDetail_1',val)}
        store={store.$table}
        columns={columns}
        columnWidth={120}
        resizable>
      </Table>
    </div>
  )
}

export default observer(FinancialFundDetailProperty)
