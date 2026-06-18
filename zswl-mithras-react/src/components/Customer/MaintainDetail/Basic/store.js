import { TableStore, ModalStore, Modal, FormStore, App, PageStore } from '@zswl/components'
import { makeAutoObservable, setSessionStorage, getSessionStorage } from '@zswl/admin'
import { message } from 'antd'
import Api from '@/api/customer/maintainApi'
import moment from 'moment'
import shareholderStore from './Shareholders/store'
import enterpriseStore from './Enterprises/store'
import { changeURLArg } from '@/utils'
import { getIsClientDetailParams } from '@/customer/CustomerUtils'

const dateFormat = 'yyyy-MM-DD'
class Store {
  constructor(id, clientType, isFormApproval, startUserId, businessVersion) {
    this.clientId = id
    this.clientType = clientType
    this.isFormApproval = isFormApproval
    this.startUserId = startUserId
    this.businessVersion = businessVersion
    makeAutoObservable(this)
  }
  clientId
  clientType
  isFormApproval
  businessVersion

  pageStore = new PageStore({})

  industry = []
  initIndustry = async () => {
    const getLocalIndustryMap = getSessionStorage('industryMap')
    if (getLocalIndustryMap) {
      this.industry = getLocalIndustryMap
      return
    }
    const list = await Api.getAllIndustry()
    this.industry = list
    setSessionStorage('industryMap', JSON.stringify(list))
  }
  businessVersion
  //地址信息
  addressInfoList = []
  addressInfo = new TableStore({
    request: async (params) => {
      if (this.isFormApproval) {
        return await Api.getApprovalAddressList({
          clientId: this.clientId,
          businessVersion: this.businessVersion,
          startUserId: this.startUserId,
          ...params,
        })
      } else {
        const data = await Api.getLegalAddressList({
          clientId: this.clientId,
          ...params,
          ...getIsClientDetailParams(),
        })
        let data1 = JSON.parse(JSON.stringify(data))
        let lastAddresslist = data1?.list.slice(-1)
        if (lastAddresslist.length > 0) {
          lastAddresslist[0].addressType = 'WORK_ADDRESS'
          delete lastAddresslist[0].id
        }
        this.addressInfoList = lastAddresslist
        return data
      }
    },
  })
  //相关信息同步
  synchronizationModal = new ModalStore({
    onFinish: async (values, { id } = {}) => {},
  })
  countryID = 156
  addressObj = {}
  //新增地址
  addressModal = new ModalStore({
    onOpen: (value) => {
      if (value) {
        if (this.isFormApproval) {
          let obj2 = {}
          let obj1 = Object.keys(value).map((key) => {
            if (key != 'area') {
              return { [key]: value[key]?.value }
            }
          })
          obj1.forEach((v) => {
            obj2 = Object.assign(obj2, v)
          })
          const { provinceName, cityName, districtName, country, province } = obj2
          this.countryID = value.country
          if (country == 156) {
            if (province == '810000' || province == '820000' || province == '710000') {
              obj2.area = [provinceName]
            } else {
              obj2.area = [provinceName, cityName, districtName]
            }
          } else {
            obj2.area = []
          }

          return obj2
        } else {
          const { provinceName, cityName, districtName, country, province } = value
          this.countryID = value.country
          if (country == 156) {
            if (province == '810000' || province == '820000' || province == '710000') {
              value.area = [provinceName]
            } else {
              value.area = [provinceName, cityName, districtName]
            }
          } else {
            value.area = []
          }
          return value
        }
      }
    },
    onFinish: async (values, { id, city, province, district, area, districtName } = {}) => {
      let current = JSON.stringify(values.area) == JSON.stringify(area)
      if (id) {
        await Api.editAddress({
          id,
          clientId: this.clientId,
          ...values,
          city: current ? city : values.area[1],
          province: current ? province : values.area[0],
          district: current ? district : values.area[2],
        })
      } else if (districtName == values.area[2]) {
        values.area = [province, city, district]
        await Api.createAddress({
          ...values,
          clientId: this.clientId,
          province: values.area && values.area[0],
          city: values.area && values.area[1],
          district: values.area && values.area[2],
        })
      } else {
        await Api.createAddress({
          ...values,
          clientId: this.clientId,
          province: values.area && values.area[0],
          city: values.area && values.area[1],
          district: values.area && values.area[2],
        })
      }

      this.addressInfo.search()
      this.addressModal.close()
      message.success('操作成功！')
    },
  })
  //删除地址信息
  removeAddress = ({ id }) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        await Api.removeAddress({ id: id?.value ?? id })
        this.addressInfo.search()
      },
    })
  }
  //工商信息
  industrialForm = new FormStore({})
  naturalForm = new FormStore({})
  industryTypeCurrent = {}
  baseInfo = {}
  setBaseInfo = (data) => {
    this.baseInfo = data
  }
  isDomestic = true
  //列表跳转详情页获取工商信息
  initDetail = async (id) => {
    const res = await Api.commerceDetail({
      clientId: id,
      startUserId: this.startUserId,
      ...getIsClientDetailParams(),
    })
    const {
      approvalDate,
      establishDate,
      industryTypeName,
      industryType,
      bizLicenseEndDate,
      industryTypeWithParent,
      belongGroupClientId,
      ...rest
    } = res
    this.setBaseInfo(res)
    this.industrialForm.setFieldsValue({
      ...rest,
      clientType: this.clientType == 'CORPORATION' ? '法人' : '自然人',
      approvalDate: approvalDate && moment(approvalDate, dateFormat),
      establishDate: establishDate && moment(establishDate, dateFormat),
      bizLicenseEndDate: bizLicenseEndDate && moment(bizLicenseEndDate, dateFormat),
      industryType: industryTypeWithParent || [],
      belongGroupClientId:
        rest.groupFlag === 1 ? { label: rest.clientName, value: id } : belongGroupClientId,
    })
    this.isDomestic = rest.domesticOrAbroad === 'DOMESTIC'
    this.setCannotEdit(rest.groupFlag === 1)
  }
  //对比接口
  compareData = {}
  getApprovalCommercedetail = async (id) => {
    const data = await Api.getApprovalCommercedetail({ clientId: id })
    this.compareData = data
  }
  // 法人工商信息保存
  legalSave = async (id, callback) => {
    this.industrialForm
      .validateFields()
      .then(async (value) => {
        value.approvalDate = value.approvalDate && moment(value.approvalDate).format(dateFormat)
        value.establishDate = value.establishDate && moment(value.establishDate).format(dateFormat)
        value.bizLicenseEndDate =
          value.bizLicenseEndDate && moment(value.bizLicenseEndDate).format(dateFormat)
        value.industryType = value.industryType && value.industryType[value.industryType.length - 1]
        value.belongGroupClientId =
          value.belongGroupClientId?.value || value.belongGroupClientId || undefined
        await Api.commerceModify({ ...value, clientId: id })
        // 创建初始化获取天眼查接口，编辑成功后 得获取详情接口，所以更改地址栏参数
        changeURLArg('flag', 'info')
        callback && callback()
        message.success('编辑成功！')
      })
      .catch((e) => {
        this.industrialForm.scrollToField(e.errorFields[0]?.name, {
          behavior(actions) {
            actions.forEach(({ el, top, left }) => {
              el.scrollTop = top + 100
              el.scrollLeft = left
            })
          },
        })
      })
  }
  // 自然人基本信息保存
  naturalSave = async (id, callback) => {
    this.naturalForm.validateFields().then(async (value) => {
      await Api.naturalModify({ ...value, clientId: id })
      changeURLArg('flag', 'info')
      callback && callback()
      message.success('编辑成功！')
    })
  }
  syncAllData = {}
  //同步功能
  synchronization = async (id) => {
    this.syncAllData = await Api.synchronization({ clientId: id })
    this.industryTypeCurrent.industryType = this.syncAllData.changedCommerceInfo.industryType
    this.industryTypeCurrent.industryTypeName =
      this.syncAllData.changedCommerceInfo.industryTypeName

    if (this.syncAllData.changedCommerceInfo.industryType == null) {
      const { industryTypeName, industryType } = await Api.commerceDetail({ clientId: id })
      this.industryTypeCurrent.industryType = industryType
      this.industryTypeCurrent.industryTypeName = industryTypeName
    }
    this.addressInfoSync.search()
    this.shareholderSync.search()
    this.affiliatedSync.search()
    //同步股东，关联企业，注册的地址
    this.synchronizationModal.open()
  }
  //同步地址信息
  addressInfoSync = new TableStore({
    pagination: false,
    request: () => {
      return this.syncAllData.registerAddressChangedItemList.list
    },
  })

  // 确认同步---地址
  synchAddress = async (values) => {
    if (values.changedType == 'ADD') {
      await Api.createAddress({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'MODIFY') {
      await Api.editAddress({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'DELETE') {
      await Api.removeAddress({
        ...values,
        clientId: this.clientId,
      })
    }
    this.addressInfo.search()
    message.success('操作成功')
  }

  // 确认同步---股东
  synchShareholder = async (values) => {
    if (values.changedType == 'ADD') {
      await Api.addShareholder({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'MODIFY') {
      await Api.editShareholder({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'DELETE') {
      await Api.removeShareholder({
        ...values,
        clientId: this.clientId,
      })
    }
    shareholderStore.shareholder.search()
    message.success('操作成功')
  }
  // 确认同步---关联企业
  synchEnterprise = async (values) => {
    if (values.changedType == 'ADD') {
      await Api.addEnterprise({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'MODIFY') {
      await Api.editEnterprise({
        ...values,
        clientId: this.clientId,
      })
    }
    if (values.changedType == 'DELETE') {
      await Api.removeEnterprise({
        ...values,
        clientId: this.clientId,
      })
    }
    enterpriseStore.affiliated.search()
    message.success('操作成功')
  }
  //同步股东信息
  shareholderSync = new TableStore({
    pagination: false,
    request: () => {
      return this.syncAllData.shareholderInfoChangedItemList.list
    },
  })
  //同步关联企业
  affiliatedSync = new TableStore({
    pagination: false,
    request: () => {
      return this.syncAllData.relatedEnterpriseChangedItemList.list
    },
  })
  regionList = []
  initRegionList = async () => {
    const list = await Api.getRegionList({ code: '156' })
    this.regionList = list.map((item) => {
      if (item.value == '810000' || item.value == '820000' || item.value == '710000') {
        return {
          ...item,
          isLeaf: true,
        }
      }
      return {
        ...item,
        isLeaf: item.leaf,
      }
    })
  }
  loadRegionListChild = async (selectedOptions) => {
    const targetOption = selectedOptions[selectedOptions.length - 1]
    targetOption.loading = true
    const data = await Api.getRegionList({ code: targetOption.value })
    let arr = data.map((v) => {
      return { ...v, isLeaf: v.leaf }
    })
    targetOption.loading = false
    targetOption.children = arr
    this.regionList = [...this.regionList]
  }
  cannotEdit = false
  setCannotEdit = (v) => {
    this.cannotEdit = v
  }
}
export default Store
