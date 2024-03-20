<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { computed } from 'vue'
import { loginAPI } from '/@/api/modules/login.ts'
import useUserStore from '/@/store/modules/user'

const userStore = useUserStore()
const rules = {
  username: {
    required: true,
    message: 'Username is required.',
    trigger: 'blur',
  },
  password: {
    required: true,
    message: 'Password is required.',
    trigger: 'blur',
  },
}
const {
  loading,
  form: model,
  send: submit,
} = useForm(
  (formData) => {
    // 可以在此转换表单数据并提交
    return loginAPI(formData)
  },
  {
    // 初始化表单数据
    initialForm: {
      username: '',
      password: '',
    },
  },
)
const disabled = computed<boolean>(
  () => model.value.username === '' || model.value.password === '',
)
const handleLogin = (e: Event) => {
  e.preventDefault()
  submit().then((res) => {
    userStore.login(res)
  })
}
</script>
<template>
  <n-h1 style="--font-size: 60px; --font-weight: 100">YUKIO</n-h1>
  <n-card size="large" style="--padding-bottom: 30px">
    <n-h2 style="--font-weight: 400">Sign-in</n-h2>
    <n-form size="large" :rules="rules" :model="model">
      <n-form-item-row label="Username" path="username">
        <n-input
          v-model:value="model.username"
          placeholder="Input your username"
        />
      </n-form-item-row>
      <n-form-item-row label="Password" path="password">
        <n-input
          v-model:value="model.password"
          type="password"
          placeholder="Input your password"
        />
      </n-form-item-row>
    </n-form>
    <n-button
      type="primary"
      size="large"
      block
      :loading="loading"
      :disabled="disabled"
      @click="handleLogin"
    >
      Sign in
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
