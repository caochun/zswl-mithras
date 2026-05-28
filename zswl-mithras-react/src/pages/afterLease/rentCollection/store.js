import { SearchBarStore, DrawerStore, ModalStore, TableStore, Modal } from '@zswl/components'
import { makeAutoObservable } from '@zswl/admin'
import Api from './api'
import { timeFormat, hasValue } from '@/utils'
import rentCollectionApi from '@/api/afterLease/rentCollectionApi'
import { uniqueId, isPlainObject, isNil } from 'lodash'
import { message } from 'antd'
import _ from 'lodash'
import DataUpload from '@/components/DataUpload'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  queryParams = { pageSize: 5, page: 1, filterConditionType: 'HIDE_FINISH' }
  setQueryParams = (val, formSearch) => {
    const { page, pageSize, filterConditionType } = this.queryParams
    if (formSearch) {
      this.queryParams = { page, pageSize, filterConditionType, ...val }
    } else {
      this.queryParams = { ...this.queryParams, ...val }
    }
    this.getList()
  }

  list
  cacheList = []
  getList = async () => {
    const res = await Api.getCollectionIndexList(this.queryParams)
    this.list = res
    // res.list.forEach(({ collectionCardList, ...rest }) => {
    //   collectionCardList.forEach((item) => {
    //     if (this.cacheList.findIndex((v) => v.id === item.id) < 0) {
    //       this.cacheList.push({ ...item, ...rest })
    //     }
    //   })
    // })
  }

  activeType = ''

  $termDetailDrawer = new DrawerStore({})
  $projectDetailDrawer = new DrawerStore({})

  searchBar = new SearchBarStore({
    onSearch: (searchData) => {
      this.setQueryParams(
        {
          ...searchData,
          page: 1,
        },
        true
      )
      return {}
    },
    optimizeParams: (params) => {
      const planCollectionDate = params.planCollectionDate
      if (planCollectionDate) {
        return {
          ...params,
          planCollectionDate: undefined,
          planCollectionDateFrom: planCollectionDate
            ? timeFormat(planCollectionDate[0])
            : undefined,
          planCollectionDateTo: planCollectionDate ? timeFormat(planCollectionDate[1]) : undefined,
        }
      }
      return params
    },
  })
  groupValue = []
  groupChange = (values) => {
    this.groupValue = values
  }
  penaltyInterest = false
  setPenaltyInterest = (val) => {
    this.penaltyInterest = val
  }
  cancel = () => {
    this.penaltyInterest = false
    this.groupValue = []
  }
  handleSubmit = async (values) => {
    const { list } = await this.interestTable.submit()
    const items = list.map(({ uuid, reducePenaltyInterest, ...rest }) => ({
      reducePenaltyInterest: Math.round(reducePenaltyInterest * 10000),
      ...rest,
    }))
    const submitValues = {
      items,
      ...values,
    }
    const formData = new FormData()
    Object.keys(submitValues).forEach((key) => {
      const val = submitValues[key]
      if (Array.isArray(val)) {
        val.forEach((item, index) => {
          if (item instanceof File || !isPlainObject(item)) {
            formData.append(key, item)
          } else {
            Object.keys(item).forEach((itemKey) => {
              if (!isNil(item[itemKey])) {
                formData.append(`${key}[${index}].${itemKey}`, item[itemKey])
              }
            })
          }
        })
      } else {
        formData.append(key, val)
      }
    })

    const res = await rentCollectionApi.postPenaltyEffect(formData)
    message.success('提交成功')
    this.cancel()
  }
  submit = () => {
    this.interestModal.open()
  }
  interestModal = new ModalStore({
    onOpen: async () => {
      const params = {
        collectionId: this.groupValue,
      }
      const res = await rentCollectionApi.postReductionList(params)
      setTimeout(() => {
        this.interestTable.setList(res.items.map((v) => ({ ...v, uuid: uniqueId() })))
      }, 10)
    },
    onFinish: async (formValues) => {
      const { list, values } = await this.interestTable.submit()
      const { fileList } = DataUpload.classify(formValues.files)

      const newFormValues = {
        ...formValues,
        files: fileList,
      }

      const required = list.some((v) =>
        _.isObject(v) ? [undefined, null].includes(v.reducePenaltyInterest) : false
      )
      const hasLimit = list.some((v) => v.applyCreditAmount > 10000 * 10000 * 10000)
      if (required) {
        message.error('请填写所有减免的罚息金额')
        return
      }
      if (hasLimit) {
        Modal.confirm({
          title: '提示',
          content: '存在合同金额> 1亿元的合同，按制度要求，本次审批流需经董事长审批，请再次确认',
          onOk: async () => {
            await this.handleSubmit(newFormValues)
            this.interestModal.close()
          },
        })
      } else {
        this.handleSubmit(newFormValues)
        this.interestModal.close()
      }
    },
  })
  interestTable = new TableStore({
    pagination: false,
  })
}
export default Store
