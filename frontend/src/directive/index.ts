import { App } from 'vue'

import outerClick from './outer-click'

export default {
  install(Vue: App) {
    //   Vue.directive('permission', permission);
    //   Vue.directive('xpack', validateLicense);
    Vue.directive('outer', outerClick)
  },
}
