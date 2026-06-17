import { observer } from '@zswl/admin'
import { Button, Input, Modal, SearchBar, Table } from '@zswl/components'
import { message } from 'antd'
import { useState } from 'react'
import styles from './index.less'

const { Item } = SearchBar

function ClientSelectModal({ store }) {
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [selectedRows, setSelectedRows] = useState([])

  const columns = [
    {
      title: '客户名称',
      dataIndex: 'clientName',
      width: 250,
    },
    {
      title: '所属主办',
      dataIndex: 'belongSponsorName',
    },
    {
      title: '所属部门',
      dataIndex: 'belongDeptName',
    },
  ]

  const rowSelection = {
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (keys, rows) => {
      setSelectedRowKeys(keys)
      setSelectedRows((prevRows) => {
        const rowMap = new Map()
        prevRows.forEach((row) => rowMap.set(row.clientId, row))
        rows.forEach((row) => rowMap.set(row.clientId, row))
        return keys.map((key) => rowMap.get(key)).filter(Boolean)
      })
    },
  }

  const handleOk = async () => {
    if (selectedRowKeys.length === 0) {
      message.error('请选择一条数据')
      return
    }
    await store.confirmClientSelect(selectedRowKeys, selectedRows)
    setSelectedRowKeys([])
    setSelectedRows([])
  }

  const handleCancel = () => {
    store.clientSelectModal.close()
    setSelectedRowKeys([])
    setSelectedRows([])
  }

  return (
    <Modal
      title="选择客户"
      store={store.clientSelectModal}
      width={900}
      destroyOnClose
      footer={[
        <Button key="cancel" onClick={handleCancel}>
          取消
        </Button>,
        <Button key="ok" type="primary" onClick={handleOk}>
          确定
        </Button>,
      ]}
    >
      <div className={styles.modalContent}>
        <Table
          rowKey="clientId"
          columns={columns}
          store={store.clientSelectTable}
          rowSelection={rowSelection}
          columnWidth={160}
          scroll={{ y: 400 }}
          searchbar={
            <SearchBar>
              <Item label="客户名称：" name="keyword">
                <Input placeholder="请输入" allowClear />
              </Item>
            </SearchBar>
          }
        />
      </div>
    </Modal>
  )
}

export default observer(ClientSelectModal)
