import { useRef, useImperativeHandle, forwardRef } from 'react'
import { observer } from '@zswl/admin'
import { NoEnumFileTable } from '@/components'
import { Button } from '@zswl/components'
import _ from 'lodash'
import Api from '../api'
import { downFile } from '@/utils'

const MODULE_TYPE = 'CONTARCT_DEPOSIT'

const Index = forwardRef(({ id, canEdit = true, businessVersion, type, processInstanceId }, ref) => {
  const tableRef = useRef()
  const params = {
    mainId: id,
    materials_type: MODULE_TYPE,
    moduleType: MODULE_TYPE,
    businessVersion,
  }
  useImperativeHandle(ref,() => {
    return {
      getSize:tableRef.current?.table?.getList
    }
  },[])
  const downloadTemplate = () => {
    Api.downloadTemplete().then((res) => {
      downFile(res)
    })
  }
  return (
    <div>
      <NoEnumFileTable
        ref={tableRef}
        functionCodeList={{
          download: 'filebatchdownload',
          remove: 'filebatchremove',
          upload: 'filematerialsupload',
          fileList: 'contarctDeposiTFileListGroup',
          batchDownload: 'filebatchdownload',
        }}
        title={'资料清单'}
        canEdit={canEdit}
        canEditItem={canEdit}
        params={params}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
        extra={[
          <Button
            onClick={downloadTemplate}
          >
            模板下载
          </Button>
        ]}
      />
    </div>
  )
})
export default observer(Index)
