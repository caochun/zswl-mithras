import { RenderColumn } from '@/components/Format'
import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import styles from './index.less'
import { saveServer } from '@/utils'

function ContractApplicationLogMortgageDiff({ detail, isLog }) {
  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>抵押措施</div>
      </div>
      <Table
              columnsFilter={'diffInfo_DiYa_1'}
              onFilter={(key,val) => saveServer('diffInfo_DiYa_1',val)}
        scroll={{ x: 2000 }}
        autoRequest={false}
        dataSource={detail}
        columns={[
          {
            title: '抵押合同编号',
            dataIndex: 'mortgageContractCode',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '抵押物清单',
            dataIndex: 'fileName',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '抵押类型',
            dataIndex: 'contractMortgageType',
            width: 120,
            render(val) {
              return (
                <RenderColumn
                  data={val}
                  isCompare={isLog}
                  selectEnum="mortgageTypeEnum"
                ></RenderColumn>
              )
            },
          },
          {
            title: '抵押物类型',
            dataIndex: 'mortgageItemType',
            width: 120,
            render(val) {
              return (
                <RenderColumn
                  data={val}
                  isCompare={isLog}
                  selectEnum="mortgageItemTypeEnum"
                ></RenderColumn>
              )
            },
          },
          {
            title: '抵押人类型',
            dataIndex: 'mortgageType',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="clientType"></RenderColumn>
            ),
          },
          {
            title: '抵押人名称',
            dataIndex: 'mortgageInfo',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => v?.map((item) => item.clientName).join(',')}
              ></RenderColumn>
            ),
          },
          {
            title: '关联合同编号',
            dataIndex: 'relatContracts',
            render: (val) => (
              <RenderColumn
                isCompare={isLog}
                data={val}
                formatText={(v) => v?.join(',')}
              ></RenderColumn>
            ),
          },
          {
            title: '抵押物描述',
            dataIndex: 'mortgageDescribe',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '是否评估',
            dataIndex: 'assess',
            render: (val) => (
              <RenderColumn isCompare={isLog} data={val} selectEnum="isConfirm"></RenderColumn>
            ),
          },
          {
            title: '评估公司',
            dataIndex: 'appraisalCompany',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '评估日期',
            dataIndex: 'assessDate',
            render: (val) => <RenderColumn isCompare={isLog} data={val}></RenderColumn>,
          },
          {
            title: '评估编号',
            dataIndex: 'appraisalCode',
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

export default observer(ContractApplicationLogMortgageDiff)
