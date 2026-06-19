import { observer } from '@zswl/admin'
import { NoEnumFileTable as FileList } from '@/components/Table'
import Collapse from '@/components/Collapse'
import { ProcessBlankBlock as BlankBlock } from '@/components/Process/ProcessEntries'
import { useFlowData } from '@/utils/domains/process/ProcessFlowContext'
import Api from '@/api/process/detail/flowDetailApi'

const IMPORTANT_TITLE_ENUM = {
  PROJ_REVIEW: {
    title: '项目重要文件',
    download: 'projReviewFileDownload',
    batchDownload: 'projReviewFileBatchDownload',
    moduleType: 'PROJ_REVIEW',
  },
  PAYMENT: {
    title: '合同重要文件',
    download: 'newContractFileDownload',
    batchDownload: 'contractFileBatchDownload',
    moduleType: 'CONTRACT',
  },
  CONTRACT: {
    title: '合同重要文件',
    download: 'newContractFileDownload',
    batchDownload: 'contractFileBatchDownload',
    moduleType: 'CONTRACT',
  },
}

const Index = ({}) => {
  const { detailData } = useFlowData()
  const { mainModule, businessKey, businessVersion, modelKey } = detailData
  const params = {
    processModuleType: modelKey,
    businessKey,
    version: businessVersion,
  }
  const current = IMPORTANT_TITLE_ENUM[mainModule]
  const importantTitle = current?.title ?? '合同重要文件'

  const getApprovalMeetingFile = async () => {
    try {
      const res = await Api.getApprovalMeetingFile(params)
      return {
        list: res?.map((item) => {
          return {
            ...item,
            id: item.fileId,
          }
        }),
      }
    } catch (err) {
      return { list: [] }
    }
  }

  const getApprovalFile = async () => {
    try {
      const res = await Api.getApprovalFile(params)
      return {
        list: res?.map((item) => {
          return {
            ...item,
            id: item.fileId,
          }
        }),
      }
    } catch (err) {
      return { list: [] }
    }
  }

  return (
    <div>
      <BlankBlock></BlankBlock>
      <Collapse header={'会议决议文件'}>
        <FileList
          canEdit={false}
          title={' '}
          tableApi={getApprovalMeetingFile}
          columns={[{ title: '资料名称', dataIndex: 'fileName' }]}
          params={{ mainId: businessKey, businessVersion, moduleType: current?.moduleType }}
          functionCodeList={{ download: current?.download, batchDownload: current?.batchDownload }}
        ></FileList>
      </Collapse>
      <BlankBlock></BlankBlock>
      <Collapse header={importantTitle}>
        <FileList
          canEdit={false}
          title={''}
          tableApi={getApprovalFile}
          columns={[{ title: '资料名称', dataIndex: 'fileName' }]}
          params={{ mainId: businessKey, businessVersion, moduleType: current?.moduleType }}
          functionCodeList={{ download: current?.download, batchDownload: current?.batchDownload }}
        ></FileList>
      </Collapse>
    </div>
  )
}

export default observer(Index)
