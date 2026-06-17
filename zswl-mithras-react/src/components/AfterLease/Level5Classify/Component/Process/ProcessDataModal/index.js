import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Button, Space } from 'antd'
import { isAssetJon } from '@/utils'
import { NoEnumFileTable } from '@/components'

const { Item } = Form

function Index({ store, canEdit = true }) {
  const { nodeItemRecord, assetClassifyId } = store
  const params = {
    mainId: assetClassifyId,
    materialsType:
      nodeItemRecord.key === 'REVIEW_MEETING'
        ? 'ASSET_CLASSIFY_REVIEW_MEETING'
        : 'ASSET_CLASSIFY_RISK_MEETING',
    materialsTypes: [
      nodeItemRecord.key === 'REVIEW_MEETING'
        ? 'ASSET_CLASSIFY_REVIEW_MEETING'
        : 'ASSET_CLASSIFY_RISK_MEETING',
    ],
    //评审会 ASSET_CLASSIFY_REVIEW_MEETING
    //风委会 ASSET_CLASSIFY_RISK_MEETING
    moduleType: 'ASSET_CLASSIFY',
  }

  return (
    <Modal
      title={`发起${nodeItemRecord.nodeLable}评审`}
      store={store.processDataModal}
      okText={'确定'}
      destroyOnClose
      width={800}
      footer={
        <Space>
          <Button onClick={store.processDataModal.close}>取消</Button>
          <Button type="primary" onClick={store.submitProcessData}>
            确定
          </Button>
        </Space>
      }
    >
      <NoEnumFileTable
        functionCodeList={{
          download: 'assetClassifyReviewFileDownload',
          remove: 'assetClassifyReviewFileBatchRemove',
          upload: 'assetClassifyReviewFileUpload',
          fileList: 'assetClassifyReviewFileList',
          batchDownload: 'assetClassifyReviewFileBatchDownload',
        }}
        title={'资料清单'}
        canEdit={canEdit && isAssetJon()}
        canEditItem={() => isAssetJon()}
        params={params}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </Modal>
  )
}

export default observer(Index)
