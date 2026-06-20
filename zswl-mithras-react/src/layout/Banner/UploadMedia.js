import { useState, useEffect, useMemo } from 'react'
import { Upload, Modal, message, Image } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { DataType } from '@/utils'
import { observer } from '@zswl/admin'
import commonApi from '@/utils/api/fileApi'

function optimizeValue(value) {
  let list = value
  if (DataType.isPlainObj(value)) {
    list = value.url
  }
  if (DataType.isStr(list)) {
    return [{ url: list, uid: list, name: list }]
  }
  if (!DataType.isArr(list)) {
    return []
  }
  return list.filter(Boolean).map((item, index) => {
    if (DataType.isStr(item)) {
      return { url: item, uid: index, name: item }
    }
    const { id, ...rest } = item
    return {
      uid: id || index,
      url: rest.preUrl,
      id,
    }
  })
}

const comParams = {
  moduleType: 'ANNOUNCEMENT',
  materialsType: 'IMAGE',
}

const MyUpload = ({ onChange, store }) => {
  const { curBannerId, bannerDetail, getBannerDetail } = store
  const [fileList, setFileList] = useState([])
  // 图片预览框
  const [previewVisible, setPreviewVisible] = useState(false)
  const [previewTitle, setPreviewTitle] = useState('')
  const [previewUrl, setPreviewUrl] = useState('')

  useEffect(() => {
    setFileList(optimizeValue(bannerDetail.images))
  }, [bannerDetail.images])

  const beforeUpload = (file, filelist) => {
    return true
  }
  // 移除
  const handleRemove = async (file) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await commonApi.postBatchRemove(
          {
            fileIds: [file.id],
            mainId: curBannerId,
            // moduleType: 'ANNOUNCEMENT',
            ...comParams,
          },
          'announcementFileBatchRemove'
        )
        message.success('删除成功')
        getBannerDetail(curBannerId)
      },
    })
  }

  const handleChange = ({ fileList: list }) => {
    const resList = list.map((item) => {
      const { response, ...other } = item
      return {
        ...other,
        ...response,
      }
    })
    return onChange?.(resList.filter((item) => item.status !== 'error'))
  }
  // 图片预览
  const handlePreview = (file) => {
    setPreviewTitle(file.name)
    setPreviewUrl(file.url || file.thumbUrl)
    setPreviewVisible(true)
  }
  // 图片预览结束/取消
  const handlePreviewCancel = () => {
    setPreviewVisible(false)
  }
  const handleCustomRequest = async (e) => {
    const { file, onError, onSuccess } = e
    commonApi
      .postFileUpload({ file, mainId: curBannerId, ...comParams }, 'announcementFileUpload')
      .then((res) => {
        onSuccess()
        message.success('上传成功！')
        getBannerDetail(curBannerId)
      })
      .catch(onError)
  }

  return (
    <div>
      <Upload
        customRequest={handleCustomRequest}
        listType="picture-card"
        multiple={true}
        fileList={fileList}
        accept="image/*"
        beforeUpload={beforeUpload}
        onRemove={handleRemove}
        onPreview={handlePreview}
        onChange={handleChange}
      >
        <div>
          <PlusOutlined />
          <div style={{ color: '#58595C' }}>点击上传图片</div>
        </div>
      </Upload>
      <Modal
        open={previewVisible}
        title={previewTitle}
        footer={null}
        onCancel={handlePreviewCancel}
      >
        <Image src={previewUrl} alt="" />
      </Modal>
    </div>
  )
}

export default observer(MyUpload)
