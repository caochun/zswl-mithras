import IconFont from '@/components/Icon'
import { observer } from '@zswl/admin'
import { App, Drawer, Table } from '@zswl/components'
import { saveServer } from '@/utils'

import classNames from 'classnames'
import { useMemo, useState } from 'react'
import styles from '../../index.less'
import store from '../../store'
const Management = () => {
  const [open, setOpen] = useState(false)
  const onClose = () => {
    setOpen(false)
  }
  const columns = useMemo(() => {
    return [
      {
        title: '时间',
        dataIndex: 'eventTime',
        width: 150,
      },
      {
        title: '事件类型',
        width: 150,
        dataIndex: 'eventType',
        tooltip: true,
      },
      {
        title: '事件名称',
        dataIndex: 'event',
        width: 140,
      },
      {
        title: '操作人',
        width: 180,
        tooltip: true,
        dataIndex: 'operator',
        render: (val) => val,
      },
      {
        title: '事件描述',
        width: 180,
        tooltip: true,
        dataIndex: 'eventdesc',
        render: (val) => val,
      },
    ]
  }, [])
  return (
    <div className={styles.moduleWrap} style={{ marginBottom: 16 }}>
      <div className={styles.title}>项目管理功能</div>
      <div className={styles.manageWrap}>
        <div
          className={styles.item}
          onClick={() => {
            console.log(1111)
            setOpen(true)
          }}
        >
          <div className={styles.icon}>
            <IconFont type="icon-wodeliucheng" />
          </div>
          <div className={styles.text}>项目旅程</div>
        </div>
        <div className={classNames(styles.item, styles.disable)}>
          <div className={styles.icon}>
            <IconFont type="icon-icon_document" />
          </div>
          <div className={styles.text}>项目文档</div>
        </div>
        <div className={classNames(styles.item, styles.disable)}>
          <div className={styles.icon}>
            <IconFont type="icon-a-icon_projectmanage" />
          </div>
          <div className={styles.text}>项目管理</div>
        </div>
      </div>
      <Drawer title="项目旅程" width={1200} placement="right" onClose={onClose} visible={open}>
        <Table
          columnsFilter={'Components_Management_1'}
          onFilter={(key, val) => saveServer('Components_Management_1', val)}
          store={store.table}
          rowKey="id"
          searchbar={{
            labelCol: { span: 6 },
            items: [
              {
                label: '时间',
                name: 'eventTime',
                type: 'rangePicker',
              },
              {
                label: '事件类型',
                name: 'eventType',
                options: App.getData().optionsType.projLifecycleEventTypeEnum,
              },
            ],
          }}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
      </Drawer>
    </div>
  )
}

export default observer(Management)
