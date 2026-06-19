import { observer } from '@zswl/admin'
import { useEffect } from 'react'
import Api from '@/api/preview/reportPreview'
import store from '../store'
const ReportPreview = ({ id, editType = 1, businessVersion, idType, forceSave }) => {
  const { onlyOfficeParams } = store
  useEffect(() => {
    store.onlyOffice({
      id,
      operate: editType,
      businessVersion,
      idType,
    })
  }, [id])
  const onDocumentReady = async () => {
    await Api.callback({ c: 'forcesave', key: onlyOfficeParams.document.key })
  }
  useEffect(() => {
    if (onlyOfficeParams && window?.DocsAPI) {
      try {
        new window.DocsAPI.DocEditor('placeholder', {
          ...onlyOfficeParams,
          events: {
            onDocumentReady: forceSave && onDocumentReady,
          },
        })
      } catch (e) {
        throw new Error(e)
      }
    }
  }, [onlyOfficeParams])
  return (
    <div style={{ height: 'calc(100vh)' }}>
      <div id="placeholder"></div>
    </div>
  )
}
export default observer(ReportPreview)
