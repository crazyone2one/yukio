<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { NFormItemRow, NH1 } from 'naive-ui'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '/@/api/modules/user'
import { useI18n } from '/@/hooks/use-i18n'
import { useUserStore } from '/@/store'

const { t } = useI18n()
const {
    // 提交状态
    loading: submiting,

    // 响应式的表单数据，内容由initialForm决定
    form: model,

    // 提交数据函数
    send: submit,

    // 提交成功回调绑定
    onSuccess,

    // 提交失败回调绑定
    onError,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        return login(formData)
    },
    {
        // 初始化表单数据
        initialForm: {
            username: '',
            password: '',
        },
    },
)
const rules = {
    username: {
        required: true,
        message: t('login.form.userName.errMsg'),
        trigger: 'blur',
    },
    password: {
        required: true,
        message: t('login.form.password.errMsg'),
        trigger: 'blur',
    },
}
const userStore = useUserStore()
const router = useRouter()
const disabled = computed(() => {
    return model.username === '' || model.password === ''
})
const handleLogin = (e: Event) => {
    e.preventDefault()
    submit(model)
}
onSuccess(async (res) => {
    userStore.loginFN(res.data)
    window.$message.info(t('login.form.login.success'))
    const route = router.currentRoute.value
    const redirect = route.query.redirect?.toString()
    await router.replace(redirect ?? route.redirectedFrom?.fullPath ?? '/')
})
onError((err) => {
    window.$message.error(t('login.form.login.errMsg'))
    console.error(err)
})
</script>
<template>
    <n-h1 style="--font-size: 60px; --font-weight: 100">YUKIO</n-h1>
    <n-card size="large" style="--padding-bottom: 30px">
        <n-h2 style="--font-weight: 400">Sign-in</n-h2>
        <n-form size="large" :rules="rules" :model="model">
            <n-form-item-row label="Username" path="username" :show-label="false">
                <n-input
                    v-model:value="model.username"
                    :placeholder="t('login.form.userName.placeholder')"
                />
            </n-form-item-row>
            <n-form-item-row label="Password" path="password" :show-label="false">
                <n-input
                    v-model:value="model.password"
                    type="password"
                    :placeholder="t('login.form.password.placeholder')"
                />
            </n-form-item-row>
        </n-form>
        <n-button
            type="primary"
            size="large"
            block
            :loading="submiting"
            :disabled="disabled"
            @click="handleLogin"
        >
            {{ t('login.form.login') }}
        </n-button>
        <br />
    </n-card>
</template>

<style scoped>
.n-h1 {
    margin: 20vh auto 20px;
    text-align: center;
    letter-spacing: 5px;
    opacity: 0.8;
}

.n-card {
    margin: 0 auto;
    max-width: 380px;
    box-shadow: var(--box-shadow);
}
</style>
