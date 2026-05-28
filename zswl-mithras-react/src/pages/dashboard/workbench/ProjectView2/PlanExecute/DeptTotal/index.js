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
              columnsFilter={'PlanExecute_DeptTotal_1'}
              onFilter={(key,val) => saveServer('PlanExecute_DeptTotal_1',val)}
        scroll={{ x: true }}
        store={deptTableStore}
        columns={[
          {
            title: '部门',
            dataIndex: 'bizDeptName',
          },
          {
            title: '投放合同数量',
            dataIndex: 'payContractQuantity',
            sorter: {
              compare: (a, b) => a.payContractQuantity - b.payContractQuantity,
              multiple: 1,
            },
          },
          AmountColumn({
            title: '计划投放金额(万元)',
            dataIndex: 'planPayAmount',
            initFormat: 1,
            sorter: {
              compare: (a, b) => a.planPayAmount?.value - b.planPayAmount?.value,
              multiple: 2,
            },
          }),
          AmountColumn({
            title: '实际投放金额(万元)',
            dataIndex: 'payAmount',
            initFormat: 1,
            sorter: {
              compare: (a, b) => a.payAmount?.value - b.payAmount?.value,
              multiple: 3,
            },
          }),
          {
            title: '达成率',
            dataIndex: 'finishRate',
            render: (value) => (value?.value ? value?.value + '%' : '-'),
          },
          {
            title: '达成率排名',
            dataIndex: 'finishRateRank',
          },
        ]}
      ></Table>
    </div>
  )
}

export default observer(Index)
