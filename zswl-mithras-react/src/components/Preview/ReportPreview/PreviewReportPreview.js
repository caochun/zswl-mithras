import { useEffect, useMemo, useState } from 'react'
import ImagePreview from './Components/Image'
import VideoPreview from './Components/Video'
import ReportPreview from './Components/Report'
import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
const Preview = ({ params: { id }, query: { editType, businessVersion, idType, forceSave } }) => {
  const data = store.page.getData()
  const CurrentPreview = useMemo(() => {
    const { previewType, url } = data

    switch (previewType) {
      case 'ONLYOFFICE':
        return (
          <ReportPreview
            id={id}
            editType={editType}
            businessVersion={businessVersion}
            idType={idType}
            forceSave={forceSave}
          />
        )
      case 'IMAGE':
        return <ImagePreview url={url} />
      case 'VIDEO':
        return <VideoPreview url={url} />
      case undefined:
      case 'UNKNOWN':
      default:
        return <div>此文件类型暂不支持预览</div>
    }
  }, [data, id])
  return (
    <Page store={store.page} params={{ id, businessVersion, idType }}>
      {CurrentPreview}
    </Page>
  )
}
export default observer(Preview)
