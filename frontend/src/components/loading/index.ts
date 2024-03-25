import { createApp, reactive } from 'vue'
import Loading from './index.vue'

const msg = reactive({
  show: false,
  tip: '加载中...',
})
const $loading = createApp(Loading, { show: msg.show, tip: msg.tip }).mount(
  document.createElement('div'),
)
const load = {
  show(tip: string = msg.tip) {
    msg.show = true
    msg.tip = tip
    document.body.appendChild($loading.$el)
  },
  hide() {
    // 控制loading隐藏的方法
    msg.show = false
  },
}
export { load }
