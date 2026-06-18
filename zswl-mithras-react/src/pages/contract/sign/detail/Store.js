import { makeAutoObservable } from '@zswl/admin'
import { TableStore, PageStore } from '@zswl/components'
import { message, Modal } from 'antd'
import Api from '@/api/contract/material'
import localApi from '../api'
import {
  ContractBizTypePriceDetailMap as bizTypePriceDetailMap,
  ContractBizTypePriceModifyMap as bizTypePriceModifyMap,
} from '@/components/Contract/ConfigEntries'
import fileListApi from '@/api/common/fileList'
import { downFile, downUrl, toHump } from '@/utils'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  signedInfo = {}
  setSignedInfo = (value) => {
    this.signedInfo = value
  }
  page = new PageStore({
    request: async ({ id, contractId }) => {
      const baseInfo = await localApi.getBaseInfo({ id: contractId })
      const priceInfo = await localApi.getContractQSDetail({ contractId })
      const bizTypeData = priceInfo[bizTypePriceDetailMap[baseInfo.bizType]]

      const res = await Api.postManageList({ id })
      const signedObj = res.list[0]
      this.setSignedInfo(signedObj)
      this.signedValue = signedObj.signingWay

      return {
        ...baseInfo,
        ...bizTypeData,
      }
    },
  })

  unSignedTable = new TableStore({
    request: () => {
      return Api.postUnSignedDetail({ mainId: this.page.getParams().id })
    },
  })

  onBatchSign = async () => {
    const { keys, rows } = this.unSignedTable.getSelected()
    if (keys?.length === 0) {
      message.info('请先勾选需要用印的文件')
      return
    }
    // const textSigningStatusList = rows.map((item) => item.textSigningStatus)
    // if (textSigningStatusList.includes('SIGNED')) {
    //   message.info('存在已签约的文件，不能重新用印')
    //   return
    // }
    // const unSignedKeys = rows.filter((item) => item.textSigningStatus != 'SIGNED')
    const { success, code, msg, data } = await Api.postBatchSign({ idList: [...keys] })
    if (success) {
      if (data) {
        Modal.info({
          title: '提示',
          width: 700,
          content: (
            <div>
              <div
                style={{ color: 'red' }}
                dangerouslySetInnerHTML={{
                  __html: data?.replace(/\n/g, '<br/>'),
                }}
              ></div>
            </div>
          ),
        })
        this.unSignedTable.search()
      } else {
        message.success('操作成功')
        this.unSignedTable.search()
      }
      return
    }
    if (code === 10086) {
      Modal.info({
        title: '批量用印失败',
        width: 700,
        content: (
          <div>
            <p>
              末能识别到以下签约方式为租赁线上签约的合同文件中的用印位置信息，请检查或添加用印位置后再进行批量用印！
            </p>
            <div
              style={{ color: 'red' }}
              dangerouslySetInnerHTML={{
                __html: msg?.replace(/\n/g, '<br/>'),
              }}
            ></div>
          </div>
        ),
      })
    } else {
      message.info(msg)
    }
  }

  downloadFile = async ({ fileId, modelType: moduleType, id }) => {
    const functionCode = 'contractTextManageFileDownload'
    const res = await fileListApi.getFileDownload({ fileId, mainId: id, moduleType }, functionCode)
    downFile(res)
  }

  onBatchDownload = async () => {
    const { keys, rows } = this.unSignedTable.getSelected()
    if (keys.length === 0) {
      message.info('请先勾选需要下载的文件')
      return
    }
    await Api.postWaitSign({
      contractId: this.page.getParams().contractId,
      fileRecordIds: keys,
    })
  }

  previewFile = (record) => {
    // 提取工具函数
    const getFileExtension = (filename) => {
      const name = filename?.value || filename
      return name?.split('.').pop()
    }
    const { materialName, fileId } = record
    if (['pdf'].includes(getFileExtension(materialName))) {
      window.open(`/preview/pdfPreview/${fileId}`)
    } else {
      window.open(`/preview/reportPreview/${fileId}`)
    }
  }

  signedValue = undefined
  changeAllSignWay = async (e) => {
    const value = e.target.value
    await Api.postWayDefault({
      mainId: this.page.getParams().id,
      textSigningWay: value,
    })
    this.signedValue = value
    message.success('更新成功')
    this.unSignedTable.search()
  }

  changeSignWay = async (params) => {
    await Api.postWaySingle(params)
    message.success('更新成功')
    this.unSignedTable.search()
  }
}
export default Store
