import { observer } from '@zswl/admin'
import Api from '@/api/cpm/payment/paymentApplicationDetail'
import { FileTable } from '@/components'
import { Button, Select, Form } from '@zswl/components'
import { useEffect, useState } from 'react'
import { message } from 'antd'
import { InputColumn } from '@/components/Format'

const MODULE_TYPE = 'PAYMENT'

const IsSigned = ({ store, canEdit }) => {
  const pageData = store.page.getData()
  const [initSigned, setInitSigned] = useState()
  const getSigned = async () => {
    if (pageData.contractId) {
      const res = await Api.postAppContractPaySign({
        contractId: pageData.contractId,
      })
      setInitSigned(res.isSigned)
    }
  }
  const updateSigned = async (value) => {
    await Api.postAppContractPaySignUpdate({
      contractId: pageData.contractId,
      isSigned: value,
    })
    message.success('更新成功')
    getSigned()
  }
  useEffect(() => {
    getSigned()
  }, [pageData])

  return (
    <div>
      是否已签约：
      <Select
        options="allowOrNo"
        onChange={updateSigned}
        value={initSigned}
        disabled={!canEdit}
      ></Select>
    </div>
  )
}

const Report = ({ store, mainId, canEditFlag: canEdit = true, businessVersion, title }) => {
  const columns = [
    { title: '资料名称', dataIndex: 'name' },
    InputColumn({ title: '上传人', dataIndex: 'createByName' }),
    InputColumn({
      title: '上传地点',
      dataIndex: 'location',
      width: 400,
    }),
    InputColumn({ title: '上传时间', dataIndex: 'createTime' }),
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    businessVersion,
  }
  const rename = (record) => {
    return (
      canEdit &&
      (record.uploadByPostList?.value
        ? record.uploadByPostList?.value?.includes('operationManagement') ||
          record.uploadByPostList?.value?.includes('projmanager')
        : record.uploadByPostList?.includes('operationManagement') ||
          record.uploadByPostList?.includes('projmanager'))
    )
  }
  const loanDownload = async () => {
    await Api.loanDownload({ id: mainId })
  }
  return (
    <>
      <FileTable
        enumType={'lendingMaterialType'}
        hasFormApproval={false}
        actions={[
          <Button type="primary" key="download" onClick={loanDownload}>
            放款表底稿下载
          </Button>,
          <IsSigned store={store} canEdit={canEdit}></IsSigned>,
        ]}
        canDelete={({ sourceBusinessKey }) => canEdit && sourceBusinessKey !== 'APP_CONTRACT'}
        rename={rename}
        uploadApi={({ file, fileType: materialsType }) =>
          Api.getPaymentMaterialsUpload({
            file,
            paymentId: mainId,
            materialsType,
          })
        }
        needBusinessVersion={(record) => {
          // const businessType = record.businessType?.value ?? record.businessType
          return false
        }}
        canBatchDownload
        params={params}
        title={title}
        canEdit={canEdit}
        columns={columns}
      />
    </>
  )
}
export default observer(Report)
