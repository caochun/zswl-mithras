import { RenderColumn } from '@/components/Format'
import { Table } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import styles from './index.less'
import { saveServer } from '@/utils'

function Index({ detail, isLog }) {
  const { bizType } = getQuery()
  const BL_ZR = bizType === 'BL' || bizType === 'ZR'

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{BL_ZR ? '债权人/债务人' : '承租人'}</div>
      </div>
      <Table
              columnsFilter={'diffInfo_ChengZuRen_1'}
              onFilter={(key,val) => saveServer('diffInfo_ChengZuRen_1',val)}
        scroll={{ x: 1200 }}
        rowKey={'phase'}
        autoRequest={false}
        dataSource={detail}
        columns={[
          {
            title: BL_ZR ? '类型' : '承租人类型',
            dataIndex: 'lesseeType',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                selectEnum={BL_ZR ? 'creditorDebtorTypeEnum' : 'lesseeypeEnum'}
              ></RenderColumn>
            ),
          },
          {
            title: BL_ZR ? '名称' : '承租人名称',
            dataIndex: 'lesseeName',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '存量风险敞口（元）',
            dataIndex: 'stockRiskExposure',
            render: (val) => <RenderColumn isCompare={isLog} data={val} formatNum></RenderColumn>,
          },
          {
            title: '指定联系人',
            dataIndex: 'contactName',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '是否上报征信',
            dataIndex: 'isReport',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="isConfirm"></RenderColumn>
            ),
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
