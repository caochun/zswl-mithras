import Api from './api'
import { makeAutoObservable, toJS } from '@zswl/admin'
import { ModalStore, FormStore, App } from '@zswl/components'
import DataUpload from '@/components/DataUpload'
import { options, downFile, setLastVisitPath, clearCookie } from '@/utils'
import { message } from 'antd'
import { rzyMemu } from '@/pages/rzy/Config'

/**
 * 将字典数据转换为 [{label, value}] 格式
 * @param {Object} dictData - 字典数据对象
 * @returns {Array} 转换后的数组格式
 */
const transformDictToOptions = (dictData) => {
  const result = {}
  Object.keys(dictData).forEach((key) => {
    result[key] = Object.entries(dictData[key]).map(([value, label]) => ({
      label,
      value,
    }))
  })
  return result
}
/**
 * 这个store主要用来存在一些全局的状态，比如个人信息，菜单栏等
 */

function loadScript(url) {
  let script = document.createElement('script')
  script.type = 'text/javascript'
  script.src = url
  document.body.appendChild(script)
}
const onlyOfficeSrc = {
  test: 'http://172.16.200.50:9080:8081',
  preSvc: 'http://onlyoffice.zsrzzl.com.cn',
  prod: 'http://onlyoffice.zsrzzl.com.cn',
  proTest: 'http://10.158.32.209:8081',
  uat: 'http://10.158.32.209:8081',
  sit: 'http://10.158.32.209:8081',
}

class Store {
  constructor() {
    makeAutoObservable(this)
  }

  getUserInfoData = {}
  noReadNum = 0
  accessList = []
  loopMenu(list) {
    return list.map((item) => {
      const { groupName, children, menuName, path, icon, functionList, code } = item
      if (children && children.length) {
        return {
          title: groupName,
          icon,
          children: this.loopMenu(children),
        }
      }
      if (functionList?.length) {
        this.accessList = this.accessList.concat(functionList.map((node) => node.code))
      }
      return {
        title: menuName,
        path,
        icon,
        code,
      }
    })
  }

  loadOnlyOfficeScript = () => {
    loadScript(
      onlyOfficeSrc[__ENV__]
        ? `${onlyOfficeSrc[__ENV__]}/web-apps/apps/api/documents/api.js`
        : 'http://172.16.200.50:9080/web-apps/apps/api/documents/api.js'
    )
  }

  init = async () => {
    const [base, optionsType, countryList, allIndustry, associationDict] = await Api.init()

    const { menuTree, user } = base
    const menu = this.loopMenu(menuTree)
    //  以下用户才能查看 “云贝”
    const allowSeeYuBeiAccount = ['admin', 'readonly', 'jifei', 'liyan', 'wujie', 'lusuping']
    if (['preSvc', 'prod'].includes(__ENV__) && allowSeeYuBeiAccount.includes(user?.account)) {
      menu.push(rzyMemu)
    }
    const allProcessTypeEnum = []
    optionsType.processModelType.forEach(({ children }) => {
      children.forEach(({ label, value }) => {
        allProcessTypeEnum.push({ label, value })
      })
    })

    const associationDictData = transformDictToOptions(associationDict)
    console.log('associationDictData: ', associationDictData)
    //获取登录信息
    this.getUserInfoData = await Api.getUserInfo()
    localStorage.setItem('userInfo', JSON.stringify(this.getUserInfoData))
    return {
      user: this.getUserInfoData,
      menu,
      access: this.accessList,
      optionsType: {
        ...options,
        ...optionsType,
        countryList,
        allIndustry,
        allProcessTypeEnum,
        ...associationDictData,
        // userList: userList.map((v) => ({ ...v, label: v.userName, value: `${v.id}` })),
      },
    }
  }
  logout = async () => {
    App.clearToken()
    clearCookie()
    //清空顶部tab页面
    sessionStorage.removeItem('LOCAL_PAGES')
    window.location.href = '/login'
  }

  ocrResult = null
  setOcrResult = (flag) => {
    this.ocrResult = flag
  }

  projProfitTool = new ModalStore({})

  createOcr = new ModalStore({
    onOpen: () => {
      this.setOcrResult(null)
    },
    onFinish: async (values) => {
      const { pdfIndex } = values
      const { fileList } = DataUpload.classify(values.file || values.files)
      const result = await Api.OCROnline({
        file: fileList,
        pdfIndex: pdfIndex
          ? pdfIndex.filter((item) => typeof item === 'string').join(',')
          : undefined,
      })
      if (result) {
        message.success('识别成功')
        this.setOcrResult(result)
      }
    },
  })
  ocrAgain = () => {
    this.setOcrResult(null)
  }
  ocrDown = async () => {
    if (this.ocrResult) {
      const res = await Api.ocrDownload({
        ids: Array.isArray(this.ocrResult) ? this.ocrResult : [this.ocrResult],
      })
      downFile(res)
    }
  }
  ocrPreview = () => {
    if (this.ocrResult) {
      window.open(`/preview/reportPreview/${this.ocrResult}`)
    }
  }
  toolModal = new ModalStore({
    onOpen: () => {},
  })
}
export default new Store()
