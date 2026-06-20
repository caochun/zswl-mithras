import { RenderColumn } from '@/components/Format'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './index.less'
import { saveServer } from '@/utils'

function Index({ detail, isLog }) {
  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>质押措施</div>
      </div>
      <Table
        columnsFilter={'diffInfo_ZhiYa_1'}
        onFilter={(key,val) => saveServer('diffInfo_ZhiYa_1',val)}
        scroll={{ x: 1200 }}
        autoRequest={false}
        dataSource={detail}
        columns={[
          {
            title: '质押合同编号',
            dataIndex: 'pledgeContractCode',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '质押类型',
            dataIndex: 'contractPledgeType',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="pledgeTypeEnum"></RenderColumn>
            ),
          },
          {
            title: '质押物清单',
            dataIndex: 'fileName',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '出质人类型',
            dataIndex: 'pledgeType',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="clientType"></RenderColumn>
            ),
          },
          {
            title: '出质人名称',
            dataIndex: 'pledgeInfo',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => v?.map((item) => item.clientName).join(',')}
              ></RenderColumn>
            ),
          },
          {
            title: '质押物描述',
            dataIndex: 'pledgeDescribe',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '是否最高额',
            dataIndex: 'highest',
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
