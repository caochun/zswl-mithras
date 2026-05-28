import { Modal, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { DetailTable } from '@/components/Table'
import { tableConfig } from './store'
import { useMemo } from 'react'
import { uniqueId } from 'lodash'

/**
 * 详情弹窗组件
 * @param {Object} props - 组件属性
 * @param {Object} props.store - 数据存储对象
 * @param {string} props.listType - 列表类型
 */
const DetailModal = observer(({ store, listType }) => {
  const {
    reportCategoryCode: type,
    processStatus,
    version,
  } = store.detailModal?.getInitialValues?.() ?? {}
  const currentConfig = tableConfig[type] || { columns: [] }
  const { columns, title, modifyApi, needAdd = true, unit, api, transform } = currentConfig
  const { currentId } = store
  const canEdit =
    ['wait', 'approval'].includes(listType) &&
    ['UN_SUBMIT', 'CANCEL', 'APPROVAL_REJECT'].includes(processStatus)
  const request = async () => {
    const response = await api?.({ reportInstanceId: currentId, version })
    // 根据不同的表格类型使用不同的转换函数

    if (transform) {
      return transform(response)
    }
    return response?.map((v) => ({ id: uniqueId(), ...v }))
  }

  const handleSave = async ({ dataList }) => {
    const { modifySuccess, errorMessageList } = await modifyApi?.({
      reportInstanceId: currentId,
      dataList,
    })

    if (!modifySuccess) {
      Modal.info({
        title: '修改失败',
        content: (
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {errorMessageList.map((item, index) => (
              <div key={item}>
                {index + 1}.{item}
              </div>
            ))}
          </div>
        ),
        width: 600,
      })
      return Promise.reject()
    }
  }
  return (
    <Modal
      title={title}
      store={store.detailModal}
      width={1000}
      footer={null}
      destroyOnClose
      bodyStyle={{ height: 700 }}
    >
      <DetailTable
        columns={columns}
        title={title}
        onImport={store.import}
        needAdd={needAdd}
        onSave={handleSave}
        unit={unit}
        canEdit={canEdit}
        onDownload={store.handleDownloadTemplate}
        request={request}
      />
    </Modal>
  )
})

export default DetailModal
