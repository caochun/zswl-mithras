/* eslint-disable */
import moment from 'moment'
import { baseURL } from '../base'

/**
 * websocket二次封装，隔离多个ws链接，处理指定消息
 *
 * @class myWebSocket
 */
type MessageFunc = (data: any, time?: moment.Moment) => void

interface IMessageList {
  [type: string]: MessageFunc[]
}
interface IRegisterList {
  [type: string]: MessageFunc[]
}

type UnSentList = { type?: string; data?: any; extend?: any }[]

enum WSStatus {
  未连接,
  连接中,
  已连接,
  断线,
  废弃,
}

interface IWsOpt {
  isOpen?: boolean // 新建ws实例后是否连接ws
  gzip: boolean
  reconnectNum?: number // 重连次数
  keepReconnect?: boolean // 是否一直重连
}

const defaults = {
  isOpen: true,
  gzip: false,
  reconnectNum: 5,
}
export class MyWebSocket {
  // @ts-ignore
  private ws: WebSocket
  private opt: IWsOpt // ws配置
  private status = WSStatus.未连接 // ws状态
  private name: string // ws名/ws地址
  private reconnectNum = 0 // 重连次数
  private timer = 0 // 重连定时器
  private messageList: IMessageList = {} // 消息处理函数集
  private registerList: IRegisterList = {} // 注册消息数据集
  private unSentList: UnSentList = [] // 未发送信息列表
  private keepReconnect = false // 是否断线后一直保持重连状态
  private token: string // token
  constructor(url: string, opt?: IWsOpt, token?: string) {
    // console.log(url)
    this.opt = { ...defaults, ...opt }
    this.name = url
    this.token = token
    this.reconnectNum = this.opt.reconnectNum!
    this.opt.isOpen && this.open()
    this.keepReconnect = !!this.opt.keepReconnect
  }

  // 连接ws
  open = async () => {
    await this.init()
  }

  // 发送信息
  send(type?: string, data?: any, extend = {}) {
    // 未连接，消息暂存未发送列表
    if (this.status !== WSStatus.已连接) {
      this.open()
      this.unSentList.push({ type, data, extend })
      return
    }

    this.ws.send(JSON.stringify({ type, data, ...extend }))
  }

  // 添加指定消息与处理函数
  message(type = 'defalut', func: MessageFunc) {
    if (!this.messageList.hasOwnProperty(type)) {
      this.messageList[type] = []
    }
    this.messageList[type].push(func)
  }

  // 注册需发送的信息，重连时重新发送
  register(types: string | string[], data?: any, extend = {}) {
    if (this.status !== WSStatus.已连接) {
      this.open()
    }
    if (typeof types === 'string') {
      this.registerList[types] = data
      data && this.send(types, data, extend)
      return
    }
    if (Array.isArray(types)) {
      types.forEach((type) => {
        this.registerList[type] = data
        data && this.send(type, data, extend)
      })
    }
  }

  // 删除指定信息
  removeMessage = (messages: string | string[]) => {
    // string
    if (typeof messages === 'string') {
      delete this.messageList[messages]

      return
    }
    // array
    messages.forEach((message) => delete this.messageList[message])
  }

  // 删除指定信息的func
  removeMessageFunc = (message: string, func: MessageFunc) => {
    const funcList = this.messageList[message]
    const len = funcList.length
    for (let i = 0; i < len; i++) {
      if (funcList[i] === func) {
        funcList.splice(i, 1)
        break
      }
    }
  }

  // 删除注册事件
  removeRegister(registers: string | string[], data?: any) {
    // string
    if (typeof registers === 'string') {
      delete this.registerList[registers]
      if (data) {
        this.send(registers, data)
      }
      return
    }

    // array
    registers.forEach((register) => delete this.registerList[register])
    if (data) {
      registers.forEach((message) => this.send(message, data))
    }
  }

  close() {
    this.messageList = {}
    this.registerList = {}
    this.ws && this.ws.close()
    // delete this.ws;
  }

  disconnect() {
    this.ws && this.ws.close()
  }

  // 测试使用 console触发ws message
  console(type: string, timestamp: string, data: any) {
    const funcList = this.messageList[type]
    const time = moment(timestamp)

    if (funcList) {
      funcList.forEach((func) => func(data, time))
    } else {
      // console.log("无法处理此类消息");
    }
  }

  // 初始化ws，绑定事件
  private init = () => {
    if (
      this.status === WSStatus.已连接 ||
      this.status === WSStatus.连接中 ||
      this.status === WSStatus.废弃
    ) {
      return
    }

    return new Promise((resolve, reject) => {
      this.status = WSStatus.连接中
      // const baseUrl = baseURL()
      // 去除 baseUrl 中的 http://
      let baseUrl = baseURL().replace(/http:\/\//, '')
      // 兼容ops 部署的环境
      if (!['preSvc', 'uat', 'prod'].includes(__ENV__) && baseUrl === '/api') {
        baseUrl = `${window.location.host}${baseUrl}`
      }

      const ws: WebSocket = new WebSocket(
        `ws://${baseUrl}${this.name}`
        // [this.token]
      )

      ws.onopen = () => {
        this.onOpen()
        resolve && resolve('')
      }

      ws.onmessage = this.dealMessage

      ws.onclose = () => {
        clearTimeout(this.timer)
        // console.log('WebSocketClosed!')
        this.status = WSStatus.断线
        this.reconnect()
      }

      ws.onerror = () => {
        // console.log('WebSocketError!')
        // console.log(`${this.name}通讯失败!!!`);
        this.status = WSStatus.断线
        this.reconnect()
        reject && reject()
      }

      this.ws = ws
    })
  }

  // 连接成功回调函数
  private onOpen = () => {
    this.status = WSStatus.已连接
    // 注册相关信息
    for (const type in this.registerList) {
      if (this.registerList.hasOwnProperty(type)) {
        this.send(type, this.registerList[type])
      }
    }
    // 发送暂存列表数据
    for (const msg of this.unSentList) {
      const { type, data, extend } = msg
      this.send(type, data, extend)
    }

    // 清空未发送列表
    this.unSentList = []
  }

  // 重连
  private reconnect() {
    if (this.status === WSStatus.连接中) {
      return
    }
    if (this.reconnectNum === 0) {
      this.status = WSStatus.废弃
      // console.log(`${this.name}已断线!!!请刷新`)
      return
    }
    if (!this.keepReconnect) {
      this.reconnectNum--
      // console.log(this.name + '5s后开始第' + this.reconnectNum + '次重连')
    }
    // console.log('ws断开，开始重连')

    this.timer = window.setTimeout(() => {
      this.open()
    }, 5000)
  }

  // 处理message
  private dealMessage = async (evt: any) => {
    let wsData = evt.data

    try {
      const data = JSON.parse(wsData)

      const funcList = this.messageList['default']
      if (funcList) {
        funcList.forEach((func) => func(data))
      }
    } catch (e) {
      // console.log(e);
    }
  }
}
