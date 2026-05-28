import { observer } from '@zswl/admin'
import { isAssetJon, isRiskSecretary } from '@/utils'
import { FileTable } from '@/components'
import { useEffect, useMemo, useState, useRef } from 'react'
import DataUpload from '@/components/DataUpload'
import Api from './api'
import { Space, message } from 'antd'
import { Button } from '@zswl/components'
import commonApi from '@/api/common/fileList'

function Index({ businessVersion, store, isFormApproval, modelKey, taskStatus }) {
  const ref = useRef()
  const { assetClassifyId } = store
  const [version, setVersion] = useState('')

  const canEdit = ['1', '7'].includes(taskStatus)

  const currentFlow = useMemo(() => {
    return modelKey === 'AssetClassifyRiskMeetingFlow'
      ? 'ASSET_CLASSIFY_RISK_MEETING'
      : 'ASSET_CLASSIFY_REVIEW_MEETING'
  }, [modelKey])

  const importSheet = async (values) => {
    const params = {
      mainId: assetClassifyId,
      materialsType:
        modelKey === 'AssetClassifyRiskMeetingFlow'
          ? 'ASSET_CLASSIFY_RISK_MEETING'
          : 'ASSET_CLASSIFY_REVIEW_MEETING',
      moduleType: 'ASSET_CLASSIFY',
    }
    const { fileList } = DataUpload.classify(values)
    await commonApi.postFileUpload(
      { materialsType: 'DEFAULT', ...params, file: fileList[0] },
      'assetClassifyReviewFileUpload'
    )
    ref.current?.table.search()
  }

  const getVersion = async () => {
    const result = await Api.postFileLastVersion({ id: assetClassifyId })
    setVersion(result)
  }

  useEffect(() => {
    if (!isFormApproval) {
      // 编辑区需要拿到版本的数据
      assetClassifyId && getVersion()
    }
    return () => {
      setVersion('')
    }
  }, [assetClassifyId])

  const params = {
    mainId: assetClassifyId,
    businessVersion: version || businessVersion,
    materialsTypes: [
      'ASSET_CLASSIFY_REVIEW_MEETING',
      'ASSET_CLASSIFY_RISK_MEETING',
      'ASSET_CLASSIFY_SUMMARY',
    ],
    moduleType: 'ASSET_CLASSIFY',
  }

  // 如果是在编辑区，没有版本，改模块就不展示了
  if ((!isFormApproval && !version) || !assetClassifyId) {
    return null
  }
  return (
    <div style={{ background: '#fff', padding: 20, marginBottom: 20 }}>
      <FileTable
        ref={ref}
        enumType={'afterLeaseReviewRiskEnum'}
        functionCodeList={{
          download: 'assetClassifyReviewFileDownload',
          remove: 'assetClassifyReviewFileBatchRemove',
          upload: 'assetClassifyReviewFileUpload',
          fileList: 'assetClassifyReviewFileListGroup',
          batchDownload: 'assetClassifyReviewFileBatchDownload',
        }}
        title={'资料清单'}
        actions={
          // 只有这2个流程的这2个岗位在审批流中可以上传
          (isRiskSecretary() || isAssetJon()) &&
          canEdit &&
          isFormApproval &&
          ['AssetClassifyRiskMeetingFlow', 'AssetClassifyReviewMeetingFlow'].includes(modelKey) && (
            <Space>
              <DataUpload maxCount={1} onChange={importSheet} accept="*">
                <Button type="primary">上传</Button>
              </DataUpload>
            </Space>
          )
        }
        canEdit={false}
        // canEditItem={(record) =>{
        //   if (record.materialSubType === 'ASSET_CLASSIFY_SUMMARY') {
        //     return true
        //   }
        //   return false
        // }}
        canDelete={(record) =>
          (isRiskSecretary() || isAssetJon()) &&
          canEdit &&
          (record.materialsType?.value === currentFlow || record.materialsType === currentFlow)
        }
        params={params}
        columns={[
          {
            title: '资料名称',
            dataIndex: 'name',
          },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </div>
  )
}

export default observer(Index)
