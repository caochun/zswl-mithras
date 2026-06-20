import { RenderColumn } from '@/components/Format'
import { Table } from '@zswl/components'
import { getQuery, observer } from '@zswl/admin'
import { formateCard } from '@/utils'
import { saveServer } from '@/utils'
import styles from './index.less'

function Index({ detail, isLog }) {
  const { bizType } = getQuery()

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>
          {bizType === 'BL' || bizType === 'ZR' ? '保理回款账户' : '保理回款账户'}
        </div>
      </div>
      <Table
        rowKey={'phase'}
        autoRequest={false}
        dataSource={detail}
        columnsFilter={'diffInfo_HuiKuan_1'}
        onFilter={(key,val) => saveServer('diffInfo_HuiKuan_1',val)}
        columns={[
          {
            title: '回款方式',
            dataIndex: 'repayWay',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="rePayType"></RenderColumn>
            ),
          },
          {
            title: '账户名称',
            dataIndex: 'accountName',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '银行账号',
            dataIndex: 'accountNum',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => formateCard(v)}
              ></RenderColumn>
            ),
          },
          {
            title: '开户行',
            dataIndex: 'accountAddress',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
