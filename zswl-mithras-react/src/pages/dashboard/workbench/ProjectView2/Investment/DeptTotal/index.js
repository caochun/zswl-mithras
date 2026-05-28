import { useEffect } from 'react'
import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { AmountColumn } from '@/components/Format'
import styles from './index.less'
import { saveServer } from '@/utils'

const Index = ({ store }) => {
  const { activityKey, deptTableStore } = store
  useEffect(() => {
    deptTableStore.search()
  }, [activityKey])
  return (
    <div>
      <div className={styles.title}>各部门统计</div>
      <Table
        columnsFilter={'Investment_DeptTotal_1'}
        onFilter={(key,val) => saveServer('Investment_DeptTotal_1',val)}
        scroll={{ x: true }}
        store={deptTableStore}
        columns={[
          {
            title: '部门',
            dataIndex: 'bizDeptName',
          },
          AmountColumn({
            title: '投放合同数量',
            dataIndex: 'payContractQuantity',
            initFormat: 1,
            sorter: {
              compare: (a, b) => a.payContractQuantity - b.payContractQuantity,
              multiple: 1,
            },
          }),
          AmountColumn({
            title: '投放金额(万元)',
            dataIndex: 'payAmount',
            initFormat: 1,
            sorter: {
              compare: (a, b) => a.payAmount?.value - b.payAmount?.value,
              multiple: 2,
            },
          }),
          AmountColumn({
            title: '加权IRR',
            dataIndex: 'averageIrr',
            initFormat: 1,
            suffix: '%',
          }),
          AmountColumn({
            title: '加权手续费率',
            dataIndex: 'averageCommissionRate',
            initFormat: 1,
            suffix: '%',
          }),
          AmountColumn({
            title: '加权合同利率',
            dataIndex: 'averageContractInterestRate',
            initFormat: 1,
            suffix: '%',
          }),
        ]}
      ></Table>
    </div>
  )
}

export default observer(Index)
