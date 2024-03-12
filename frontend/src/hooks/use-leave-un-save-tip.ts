import { onBeforeRouteLeave } from 'vue-router'
import { ref } from 'vue'
import { useI18n } from '/@/hooks/use-i18n.ts'

const isSave = ref(false)
export default function () {
    const { t } = useI18n()
    const setState = (flag: boolean) => {
        isSave.value = flag
    }
    onBeforeRouteLeave((to, from, next) => {
        if (to.path === from.path) {
            next()
            return
        }
        if (!isSave.value) {
            window.$dialog.error({
                title: t('common.unSaveLeaveTitle'),
                content: t('common.unSaveLeaveContent'),
                positiveText: t('common.leave'),
                negativeText: t('common.cancel'),
                onPositiveClick: async () => {
                    isSave.value = true
                    next()
                },
            })
        } else {
            next()
        }
    })
    return {
        setState,
    }
}
