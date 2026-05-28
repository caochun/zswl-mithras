import { observer, getQuery } from '@zswl/admin'
import { Page, Modal, Form, DatePicker, Button, Select } from '@zswl/components'
import { Input, message } from 'antd'
import { useEffect, useMemo, useRef, useState } from 'react'
import Store from './store'
import BaseInfo from './BaseInfo'
import BaseInfoTable from './BaseInfoTable'
import CollectionRecord from './CollectionRecord'
import { NoEnumFileTable } from '@/components'
import { dateTransform } from '@/utils'
import collectionManagementApi from '@/api/overdue/collectionManagementApi'
import store from '../store'

const { TextArea } = Input
const Detail = ({ params: { id }, query: { bizType, newProject, canEditFlags = 'true' } }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'
  const baseInfoDetail = store.page.getData()
  const { clientId } = baseInfoDetail
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth = canEditFlags === 'true'

  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
      extra={[
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'NON_LIVE' })}>
          非现场催收
        </Button>,
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'LIVE' })}>
          现场催收
        </Button>,
        <Button type="primary" onClick={() => store.collectionModal.open({ type: 'SEND_LETTER' })}>
          发函催收
        </Button>,
      ]}
    >
      <BaseInfo dataSource={baseInfoDetail} canEdit={canEditFlagsFormAuth} store={store} />
      <BaseInfoTable canEdit={canEditFlagsFormAuth} mainId={id} store={store} />
      <CollectionRecord canEdit={canEditFlagsFormAuth} mainId={id} store={store} />
      <CollectionModal
        modal={store.collectionModal}
        ocId={id}
        clientId={clientId}
        store={store}
        canEdit={canEditFlags}
      />
    </Page>
  )
}

export default observer(Detail)

export const CollectionModal = observer(
  ({ modal, clientId, ocId, modalProps = {}, canEdit, store }) => {
    const { type, id, isArchive } = modal?.getInitialValues() ?? {}
    const [contractOptions, setContractOptions] = useState([])
    useEffect(() => {
      getContractList(clientId).then(setContractOptions)
    }, [clientId])
    const titleMap = {
      NON_LIVE: '非现场催收',
      LIVE: '现场催收',
      SEND_LETTER: '发函催收',
    }
    const fileTableRef = useRef(null)
    const isSendLetter = type === 'SEND_LETTER'
    const params = {
      mainId: id,
      moduleType: 'OVERDUE_COLLECTION_ACTION',
    }
    const save = async (needMessage = true) => {
      const formStore = modal.getFormStore()
      await formStore.validateFields()
      const data = formStore.getFieldsFormatValue()
      const contractCodes = contractOptions
        .filter(({ value }) => (data?.contractIds ?? []).includes(value))
        .map(({ label }) => label)

      await collectionManagementApi.postActionUpdate({ ...data, contractCodes, ocId })
      if (needMessage) {
        await store?.page.init()
        store?.collectionRecordTable?.search()
        message.success('保存成功')
      }
    }
    const isFormApproval = getQuery('typeId') == 'approval'
    const generateReport = async () => {
      await save(false)
      const res = await collectionManagementApi.postLetterGenerate({
        id,
      })
      fileTableRef.current.table.search()
    }
    return (
      <Modal
        title={titleMap[type]}
        store={modal}
        width={800}
        footer={
          isArchive
            ? null
            : [
                !isFormApproval && <Button onClick={modal.close}>取消</Button>,
                canEdit && (
                  <Button type="primary" onClick={save}>
                    保存
                  </Button>
                ),
                !isFormApproval && isSendLetter && (
                  <Button
                    type="primary"
                    onClick={async () => {
                      await save(false)
                      await modal.submit()
                    }}
                  >
                    提交
                  </Button>
                ),
              ]
        }
        destroyOnClose
        {...modalProps}
      >
        <Form>
          <Form.Item name="id" hidden />
          <Form.Item name="type" hidden />

          {!isArchive && !isSendLetter && (
            <>
              <Form.Item
                label="催收日期"
                name="date"
                transform={(val) => dateTransform(val, 'date')}
                rules={[{ required: true, message: '请选择催收日期' }]}
              >
                <DatePicker disabled={!canEdit} />
              </Form.Item>
              <Form.Item
                label="催收进展"
                name="describe"
                rules={[{ required: true, message: '请输入催收进展' }]}
              >
                <TextArea disabled={!canEdit} maxLength={500} />
              </Form.Item>
            </>
          )}
          {!isArchive && isSendLetter && (
            <>
              <Form.Item
                label="发函日期"
                name="date"
                transform={(val) => dateTransform(val, 'date')}
                rules={[{ required: true, message: '请选择发函日期' }]}
              >
                <DatePicker disabled={!canEdit} />
              </Form.Item>
              <Form.Item
                label="发函类型"
                name="letterType"
                rules={[{ required: true, message: '请选择发函类型' }]}
              >
                <Select
                  options={'letterType'}
                  disabled={!canEdit}
                  getPopupContainer={() => document.body}
                />
              </Form.Item>
              <Form.Item
                label="合同编号"
                name="contractIds"
                rules={[{ required: true, message: '请选择合同编号' }]}
              >
                <Select
                  options={contractOptions}
                  mode="multiple"
                  getPopupContainer={() => document.body}
                  disabled={!canEdit}
                />
              </Form.Item>
              <Form.Item
                label="发函原因"
                name="describe"
                rules={[{ required: true, message: '请输入发函原因' }]}
              >
                <Input.TextArea disabled={!canEdit} maxLength={500} />
              </Form.Item>
            </>
          )}
          {isSendLetter && (
            <NoEnumFileTable
              title={'发函文件'}
              canEdit={canEdit}
              extra={[
                canEdit && (
                  <Button type="primary" onClick={generateReport}>
                    生成函件
                  </Button>
                ),
              ]}
              columns={[{ title: '文件名称', dataIndex: 'filename' }]}
              ref={fileTableRef}
              canBatchDownload
              needApproval={false}
              params={{ ...params, materialsType: 'COLLECTION' }}
            />
          )}
          <NoEnumFileTable
            title={'资料清单'}
            canEdit={canEdit}
            columns={[{ title: '附件名称', dataIndex: 'filename' }]}
            canBatchDownload
            needApproval={false}
            canEditItem={false}
            params={{ ...params, materialsType: 'ENCLOSURE' }}
          />
        </Form>
      </Modal>
    )
  }
)

export const getContractList = async (clientId) => {
  if (!clientId) return []
  const res = await collectionManagementApi.getContractPulldown({ clientId })
  const list = Object.entries(res).map(([value, label]) => ({ label, value: Number(value) }))
  return list
}
