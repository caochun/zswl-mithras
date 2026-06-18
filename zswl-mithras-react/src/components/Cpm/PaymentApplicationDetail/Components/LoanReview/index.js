import { observer } from '@zswl/admin'
import Api from '@/api/cpm/payment/paymentApplicationDetail'
import { NoEnumFileTable } from '@/components'
import { Button } from '@zswl/components'
import { message } from 'antd'

import { useRef } from 'react'

const MODULE_TYPE = 'PAYMENT'
const Report = ({ id: mainId, canEdit = true }) => {
  const fileRef = useRef()
  const generate = async () => {
    await Api.loanReviewFile({ id: mainId })
    message.success('操作成功')
    fileRef.current.table.search()
  }

  const columns = [
    {
      title: '资料名称',
      dataIndex: 'name',
      render: (value, record) => {
        const title = (
          <div>
            {record?.systemGenerate ? (
              <div>
                {value} <span className={'z-tag'}>自动生成</span>
              </div>
            ) : (
              <span>{value}</span>
            )}
          </div>
        )
        const render = fileRef.current.filedRender
        return render(title, record, {
          dataIndex: 'name',
        })
      },
    },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  const params = {
    mainId,
    moduleType: MODULE_TYPE,
    materialsType: 'LOAN_REVIEW',
  }

  const tableApi = async () => {
    const res = await Api.loanReviewFileList({ id: mainId })
    return {
      list: res.map((item) => {
        return {
          ...item,
          id: item.id,
          name: item.filename,
        }
      }),
    }
  }
  return (
    <NoEnumFileTable
      title="放款审核表"
      ref={fileRef}
      extra={[
        <DownloadTemplate templateDownApi={() => Api.loanReviewDownloadTemplate({ id: mainId })} />,
        <Button
          type="primary"
          disabled={!canEdit}
          key="generate"
          onClick={generate}
          access={'paymentloanReviewFile'}
        >
          文件生成
        </Button>,
      ]}
      canBatchDownload={false}
      tableApi={tableApi}
      canEdit={canEdit}
      columns={columns}
      params={params}
      functionCodeList={{
        upload: 'fileuploadloanReviewFile',
      }}
    />
  )
}
export default observer(Report)
