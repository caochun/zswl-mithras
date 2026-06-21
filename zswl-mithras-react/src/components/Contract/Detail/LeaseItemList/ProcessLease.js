import { observer } from '@zswl/admin'
import { Modal, Button, Table } from '@zswl/components'
import { FiledFormat } from '@/components/Format'
import { useEffect, useMemo } from 'react'
import { Space, Empty, Alert } from 'antd'
import { saveServer } from '@/utils'

const ContractProcessLeaseSelector = ({ store }) => {
  const { chooseHeader, processLeaseTable, cacheChooseId } = store
  const columns = useMemo(() => {
    return chooseHeader?.map((item) => {
      return {
        title: item,
        dataIndex: item,
        width: item.indexOf('序号') > -1 ? 80 : 180,
        render: (val) => <FiledFormat value={val} />,
      }
    })
  }, [JSON.stringify(chooseHeader)])

  // 选中
  // useEffect(() => {
  //   const list = processLeaseTable.getList()?.filter((item) => item.hasChoose === 1)
  //   processLeaseTable.setSelected(list?.map((item) => item.id))
  // }, [processLeaseTable.loading, JSON.stringify(processLeaseTable.getList())])

  return (
    <Modal
      width={800}
      store={store.processLeaseModal}
      title="选择租赁物"
      getContainer={() => document.body}
      footer={
        store.processLeaseTable.getList()?.length > 0 ? (
          <Space>
            <Button key="cancel" onClick={store.processLeaseModal.close}>
              取消
            </Button>
            <Button key="confirm" onClick={store.onProcessSelect} type="primary">
              确定
            </Button>
          </Space>
        ) : null
      }
    >
      <Table
              columnsFilter={'detail_ZuLinWu_ProcessLease'}
              onFilter={(key,val) => saveServer('detail_ZuLinWu_ProcessLease',val)}
        title={() => {
          if (!cacheChooseId.length) return null
          return (
            <Alert
              type="info"
              message={
                <Space>
                  <div>{`已选${cacheChooseId?.length}项`} </div>
                  <Button type="link" onClick={store.cancelChooseSelected}>
                    取消选择
                  </Button>
                </Space>
              }
            ></Alert>
          )
        }}
        rowKey={'id'}
        searchbar={{
          searchButton: false,
          resetButton: false,
          labelCol: { span: 4 },
          items: [
            {
              label: '名称',
              name: 'name',
            },
          ],
        }}
        rowSelection={{
          type: 'checkbox',
          // onChange: store.onChooseChange,
          onSelect: store.onChooseSelect,
          onSelectAll: store.onChooseSelectAll,
          getCheckboxProps: (record) => {
            return {
              disabled: record.canChoose !== 1,
            }
          },
        }}
        store={store.processLeaseTable}
        columns={columns}
        columnWidth={180}
        scroll={{
          x: 1500,
          y: 400,
        }}
      />
    </Modal>
  )
}

export default observer(ContractProcessLeaseSelector)
