import { history, observer } from '@zswl/admin'
import { Button, Form, Modal, ModalStore, Table, TableStore, Upload } from '@zswl/components'
import { Input, Radio, Space, message } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { BlackGrayColumns as ALl_COLUMNS } from '@/components/BlackGray/BlackGrayEntries'
import { getTableColumns } from '@/utils'
import recordTableApi from '@/api/blackGray/recordTableApi'
import moment from 'moment'
import { saveServer } from '@/utils'

const nameColumns = [
  { title: '企业名称', search: true },
  { title: '统一社会信用代码', search: false },
  { title: '业务类型', search: false },
  { title: '黑灰标识', search: false },
  '观察期',
  { title: '申请原因', rename: '入库原因', search: false },
  { title: '入库时间', search: false },
  '业务规模（万元）',
]
const columns = getTableColumns(ALl_COLUMNS, nameColumns, true)

const FailColumns = getTableColumns(ALl_COLUMNS, [
  '企业名称',
  '统一社会信用代码',
  '以下字段校验不通过',
])
const BatchModal = observer(({ modal, store }) => {
  const data = modal.getInitialValues()
  const table = useMemo(
    () =>
      new TableStore({
        request: (params) => {
          let list = data?.addList ?? []
          if (params.enterpriseName) {
            list = list.filter((item) => item.enterpriseName.includes(params.enterpriseName))
          }

          return list
        },
      }),
    [data?.addList]
  )
  const FailModal = useMemo(() => new ModalStore(), [])
  const failTable = useMemo(() => new TableStore({}), [])
  const openFailModal = () => {
    FailModal.open()
    setTimeout(() => {
      failTable.setList(data?.errorList)
    }, 10)
  }

  const submit = async () => {
    const taskNum = store.page.getData().taskNum
    const newParams = (data?.addList ?? []).map((v) => ({
      ...v,
      taskNum,
      source: 'INTERNAL_APPROVAL',
    }))
    await recordTableApi.postBatchAdd(newParams)
    message.success('批量添加成功')
    store.table.search()
    modal.close()
  }
  return (
    <Modal
      store={modal}
      title="名单导入"
      width={1000}
      footer={[
        <Button onClick={submit} key="submit" type="primary">
          确认
        </Button>,
      ]}
    >
      <div style={{ height: 500, overflowY: 'scroll', overflowX: 'hidden', paddingRight: 6 }}>
        <Table
        columnsFilter={'mainTask_detail_SubmitRadio_1'}
                onFilter={(key,val) => saveServer('mainTask_detail_SubmitRadio_1',val)}
        
          columns={columns}
          store={table}
          editable={false}
          columnWidth={120}
          actions={[
            <div key={'1'}>
              识别成功<span style={{ color: '#275bdf' }}>{data?.addList?.length ?? 0}</span>
              条数据，失败
              <span style={{ color: 'red', margin: '0 4px' }}>{data?.errorList?.length ?? 0}</span>
              条数据
            </div>,
            !!data?.errorList?.length && (
              <Button onClick={openFailModal} key="fail">
                查看失败数据
              </Button>
            ),
            // <Button.Search key="search" onClick={search}>
            //   查询所属集团
            // </Button.Search>,
          ]}
        ></Table>
      </div>
      <Modal store={FailModal} title="识别失败清单" width={1000} footer={null}>
        <div style={{ color: 'red' }}>以下数据未完成导入,请检查后重新导入</div>
        <Table onFilter={(key,val) => saveServer('mainTask_detail_SubmitRadio_2',val)} columnsFilter={'mainTask_detail_SubmitRadio_2'} columns={FailColumns} store={failTable} scroll={{ x: 'auto' }}></Table>
      </Modal>
    </Modal>
  )
})
const Index = ({ value, onChange, store }) => {
  const batchModalStore = useMemo(() => new ModalStore(), [])
  const [isLoading, setIsLoading] = useState(false)
  const importTpl = async (file) => {
    setIsLoading(true)
    try {
      let data = await recordTableApi.postAnalysisUpload({ file, source: 'INTERNAL_UPLOAD' })
      data.addList = data?.addList?.map(({ applyReasonType, ...item }) => {
        item.applyReasonType = [applyReasonType]
        item.warehouseTime =
          moment(item.warehouseTime) < moment().endOf('day')
            ? moment(item.warehouseTime).format('yyyy-MM-DD')
            : moment().format('yyyy-MM-DD')

        return item
      })
      batchModalStore.open(data)
      setIsLoading(false)
      return false
    } catch {
      setIsLoading(false)
      return false
    }
  }
  return (
    <Space>
      <Radio.Group onChange={onChange} value={value}>
        <Radio value={1}>单个录入</Radio>
        <Radio value={2}>批量导入</Radio>
      </Radio.Group>
      {value === 1 ? (
        <Button.Add onClick={() => store.singeModalStore.open({})}>单个录入</Button.Add>
      ) : (
        <>
          <Upload beforeUpload={importTpl} key={'import'} showUploadList={false} accept=".xlsx">
            <Button.Upload type={'primary'} loading={isLoading}>
              批量导入
            </Button.Upload>
          </Upload>
          <Button.Download onClick={recordTableApi.getTemplateDownload}>下载模板</Button.Download>
        </>
      )}
      <BatchModal modal={batchModalStore} store={store} />
    </Space>
  )
}
export default observer(Index)
