import { observer } from '@zswl/admin'
import styles from './index.less'
import { NoEnumFileTable } from '@/components/Table'
import Api from '@/api/contract/material'
import { Access } from '@zswl/components'
import { InputColumn } from '@/components/Format'

const Index = ({ store }) => {
  const { id, contractId } = store.signedInfo
  return (
    <div className={styles.fileWrap}>
      <div className={styles.title}>合同相关材料</div>
      <NoEnumFileTable
        title={<div className={'z-sub-title'}>已签约</div>}
        tableApi={() => Api.postSignedDetail({ mainId: id })}
        canBatchDownload
        canDelete={(record) =>
          record.createByName && Access.validate('contractTextManageFileBatchRemove')
        }
        canUpload={Access.validate('contractTextManageFileUpload')}
        params={{ mainId: id, moduleType: 'CONTRACT_TEXT_MANAGE' }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
      <NoEnumFileTable
        tableApi={() => Api.postAndVideos({ contractId })}
        title={<div className={'z-sub-title'}>合同签署照片和视频</div>}
        canBatchDownload
        canEdit={false}
        // canDelete={Access.validate('contractFileBatchRemove')}
        // canUpload={Access.validate('contractFileUpload')}
        params={{ mainId: contractId, moduleType: 'CONTRACT' }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          InputColumn({ title: '上传时间', dataIndex: 'createTime' }),
          InputColumn({ title: '上传地点', dataIndex: 'location', width: 400 }),
          InputColumn({ title: '上传人', dataIndex: 'createByName' }),
        ]}
      />
    </div>
  )
}

export default observer(Index)
