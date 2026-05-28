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
    <Tooltip
      title={<div dangerouslySetInnerHTML={{ __html: htmlString }}></div>}
      placement="topLeft"
    >
      <div dangerouslySetInnerHTML={{ __html: htmlString }} className={styles.renderRow}></div>
    </Tooltip>
  )
}

const Index = ({ store }) => {
  return (
    <div>
      <h4>工商信息</h4>
      <Table
        columnsFilter={'CheckBusiness_CompareInfo_CompareTable'}
        onFilter={(key, val) => saveServer('CheckBusiness_CompareInfo_CompareTable', val)}
        columnWidth={200}
        store={store.tableStore}
        columns={[
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

export default observer(Index)
