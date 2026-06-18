import { DynamicDesc } from '@/components/Table'
import { useEffect, useState } from 'react'
import Api from '@/api/afterLease/level5Classify'
import { FileTable } from '@/components/Table'
import { FileCommonParams } from '../../Level5ClassifyConfig'
import styles from '../index.less'

const Index = ({ store, canEdit = true }) => {
  const { id: businessId, modelKey, businessVersion } = store.page.getParams()
  const { isProjSponsor } = store.page.getData()
  const [contentData, setContentData] = useState({})

  const getData = async () => {
    const res = await Api.getCheckReport({
      assetClassifyClientId: businessId,
      processType: modelKey,
      businessVersion,
    })
    setContentData(res)
  }
  const saveContent = async (data) => {
    const param = {
      contentList: data,
      assetClassifyClientId: businessId,
    }
    await Api.modifyCheckReport(param)
    getData()
  }
  useEffect(() => {
    if (businessId) {
      getData()
    }
  }, [businessId])

  const download = async ({ id }) => {
    await Api.postFileDown({
      fileId: id,
      mainId: businessId,
      materialsType: 'ASSET_CLASSIFY_CHECK_REPORT',
      ...FileCommonParams.chufen,
    })
  }

  const params = {
    mainId: businessId,
    businessVersion,
    materialsTypes: ['ASSET_CLASSIFY_CHECK_REPORT'],
    ...FileCommonParams.chufen,
  }
  return (
    <div className={styles.content}>
      {contentData?.assetClassifyCheckContent?.length > 0 && (
        <div className={styles.dynamicDesc}>
          <DynamicDesc
            title={
              <div className={styles.title}>
                <span>检查内容</span>
                {contentData.title && (
                  <span className={styles.subTitle}>{`（来源：${contentData.title}）`}</span>
                )}
              </div>
            }
            contentData={contentData?.assetClassifyCheckContent}
            saveApi={saveContent}
            canEdit={canEdit}
          />
        </div>
      )}

      <div>
        <FileTable
          enumType={'assetClassifyCheckMaterialsEnum'}
          title={<div className="z-title">附件</div>}
          canEdit={canEdit && isProjSponsor}
          uploadApi={({ file, fileType }) =>
            Api.postFileUpload({
              file,
              mainId: businessId,
              materialsType: 'ASSET_CLASSIFY_CHECK_REPORT',
              materialsSubType: fileType,
              ...FileCommonParams.chufen,
            })
          }
          params={params}
          columns={[
            {
              title: '资料名称',
              dataIndex: 'name',
            },
            {
              title: '上传人',
              dataIndex: 'createByName',
            },
            {
              title: '上传时间',
              dataIndex: 'createTime',
            },
          ]}
        />
      </div>
    </div>
  )
}

export default Index
