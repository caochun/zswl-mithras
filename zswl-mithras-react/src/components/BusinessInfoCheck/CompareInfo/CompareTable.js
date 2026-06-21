import { Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Tooltip } from 'antd'
import styles from '../index.less'
import { saveServer } from '@/utils'

const render = (value) => {
  const newValue = Array.isArray(value) ? value : [value]
  const htmlString = newValue.join('')
  const nonTagContent = htmlString.replace(/<[^>]+>/g, '')
  return (
    <Tooltip title={nonTagContent} placement="topLeft">
      <div dangerouslySetInnerHTML={{ __html: htmlString }} className={styles.renderRow}></div>
    </Tooltip>
  )
}

const BusinessInfoCheckCompareTable = ({ store }) => {
  return (
    <div>
      <h4>工商信息</h4>
      <Table
        columnWidth={200}
        store={store.tableStore}
        columnsFilter={'components_CompareTable_1'}
        onFilter={(key,val) => saveServer('components_CompareTable_1',val)}
        columns={[
          {
            title: '检验对象',
            dataIndex: 'clientType',
            width: 80,
            onCell: (_, index) => ({
              rowSpan: index % 3 === 0 || index === 0 ? 3 : 0,
            }),
          },
          {
            title: '字段名称',
            dataIndex: 'fieldName',
            width: 80,
          },
          {
            title: '系统内容',
            dataIndex: 'systemValue',
            render,
          },
          {
            title: '天眼查数据',
            dataIndex: 'tycValue',
            render,
          },
          {
            title: '不一致内容',
            dataIndex: 'compareValue',
            render,
          },
        ]}
      ></Table>
    </div>
  )
}

export default observer(BusinessInfoCheckCompareTable)
