import React from 'react'
import { observer } from '@zswl/admin'
import { Typography } from 'antd'
import { Descriptions, Table } from '@zswl/components'
import numeral from 'numeral'
import styles from './styles.less'
import LoanGuaranteeInformation from './LoanGuaranteeInformation'
import { saveServer } from '@/utils'

function Summary({ store, id }) {
  const columns = [
    { title: '首次有信贷交易的年份', dataIndex: 'firstCreditYear', align: 'center' },
    { title: '发生信贷交易的机构数', dataIndex: 'institutionCount', align: 'center' },
    {
      title: '当前有未结清信贷交易的机构数',
      dataIndex: 'unsettledCreditInstitutionCount',
      align: 'center',
    },
    { title: '首次有关还款责任的年份', dataIndex: 'firstRepaymentYear', align: 'center' },
  ]

  return (
    <div id={id} className={styles.creditInfoContainer}>
      <Typography.Title level={5}>信息概要</Typography.Title>
      <Table         columnsFilter={'ZX_Summary_1'}
        onFilter={(key,val) => saveServer('ZX_Summary_1',val)} store={store} bordered columns={columns} pagination={false} rowKey="key" />
      <LoanGuaranteeInformation />
      <Table
              columnsFilter={'ZX_Summary_2'}
              onFilter={(key,val) => saveServer('ZX_Summary_2',val)}
        store={store}
        style={{ marginTop: 28 }}
        bordered
        columns={columns}
        pagination={false}
        rowKey="key"
      />
    </div>
  )
}

export default observer(Summary)
