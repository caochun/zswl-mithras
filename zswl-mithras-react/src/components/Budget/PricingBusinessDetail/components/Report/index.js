import { observer } from '@zswl/admin'
import Api from '@/api/budget/pricing/ftpMaterialsFile'
import { NoEnumFileTable } from '@/components/Table'
import { useCallback } from 'react'

function Index({ id, canEdit = true, businessVersion, bizType = 'FTP_QUARTERLY_GUIDANCE' }) {
  const upload = async (record) => {
    const { file } = record
    const params = {
      file,
      belongId: id,
      bizType,
    }
    return Api.postFileUpload(params)
  }

  const params = {
    mainId: id,
    moduleType: bizType,
    businessVersion,
  }
  return (
    <NoEnumFileTable
      title={<div className="z-title">资料清单</div>}
      canEdit={canEdit}
      uploadApi={upload}
      params={params}
      columns={[
        {
          title: '资料名称',
          dataIndex: 'name',
        },
      ]}
    />
  )
}

export default observer(Index)
