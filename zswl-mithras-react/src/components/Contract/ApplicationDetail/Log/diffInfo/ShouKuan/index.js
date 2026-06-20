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
          {bizType === 'BL' || bizType === 'ZR' ? '卖方收款账户' : '收款账户'}
        </div>
      </div>
      <Table
              columnsFilter={'diffInfo_ShouKuan_1'}
              onFilter={(key,val) => saveServer('diffInfo_ShouKuan_1',val)}
        rowKey={'phase'}
        autoRequest={false}
        dataSource={detail}
        columns={[
          // {
          //   title: '客户名称',
          //   dataIndex: 'clientName',
          //   render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          // },
          {
            title: '收款方',
            dataIndex: 'payeeType',
            render: (v, t) => {
              return (
                <RenderColumn
                  data={v}
                  isCompare={isLog}
                  selectEnum="contractAccountPayeeTypeEnum"
                ></RenderColumn>
              )
            },
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
