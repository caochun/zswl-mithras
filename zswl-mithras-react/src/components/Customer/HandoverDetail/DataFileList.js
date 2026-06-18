import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components/Table'
import customerMaintainApi from '@/api/customer/maintainApi'
import { getUserInfo } from '@/utils'

const Report = ({ canEdit = true, store }) => {
  const { processInstanceId } = store.page.getParams()
  const { batchNoInfo } = store.page.getData()
  const columns = [
    {
      title: '资料名称',
      dataIndex: 'filename',
    },
    {
      title: '上传人',
      dataIndex: 'createByName',
    },
    {
      title: '上传时间',
      dataIndex: 'createTime',
    },
  ]

  const params = {
    mainId: batchNoInfo.id,
    moduleType: 'CLIENT_TRANSFER',
    materialsType: 'OTHER',
    processInstanceId,
  }
  return (
    <NoEnumFileTable
      title={'补充资料'}
      canEdit={canEdit}
      columns={columns}
      params={params}
      functionCodeList={{
        upload: 'fileupload-clienttransfer',
        download: 'filedownload-clienttransfer',
        remove: 'filebatchremove-clienttransfer',
      }}
      tableApi={() => customerMaintainApi.postTransferFileList({ ...params })}
      canEditItem={false}
      canDelete={(record) => {
        return record.createBy == getUserInfo().id
      }}
    />
  )
}
export default observer(Report)
