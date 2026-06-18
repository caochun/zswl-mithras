import { observer } from '@zswl/admin'
import Api from '@/api/afterLease/rentalInspectionReport'
import commonApi from '@/api/common/materialsApi'
import { FileTable } from '@/components/Table'

const enumType = [{ label: '财务数据', value: 'CHECK_REPORT_PUBLIC_FINANCE' }]
const businessType = 'NEW_AFTER_LEASE_CHECK_REPORT'
function Index({ id, canEdit = true, businessVersion }) {
  const param = {
    mainId: id,
    moduleType: businessType,
    businessVersion,
    materialsTypes: ['CHECK_REPORT_PUBLIC_FINANCE'],
  }
  const upload = async (record) => {
    const { file, fileType: materialsType } = record
    const params = {
      file,
      belongId: id,
      materialsType,
      businessType,
    }
    return commonApi.postMaterialsUpload(params, 'afterleasepublicfinancematerialsupload')
  }

  return (
    <FileTable
      enumType={enumType}
      title={<div className="z-title">财务报表</div>}
      canEdit={canEdit}
      uploadApi={upload}
      params={param}
      columns={[
        {
          title: '项目评审资料',
          dataIndex: 'name',
        },
      ]}
    />
  )
}

export default observer(Index)
