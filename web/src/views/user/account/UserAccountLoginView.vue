<template>
  <ContentField class="loginform" v-if="!$store.state.user.pulling_info">
    <div class="row justify-content-md-center">
      <div class="col-12">
        <form @submit.prevent="login">
          <div class="mb-3 text-center">
            <span class="text-dark font-size-h1">King Of Bots</span>
          </div>
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="username">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="password">
          </div>
          <div class="error-message">{{error_message}}</div>
          <div class="mb-0 btn_login">
            <button type="submit" class="btn btn-block btn-primary font-w400">登录</button>
          </div>
        </form>
      </div>
    </div>
    <div class="row p-2 px-1">
      <div class="col-2">
        <router-link class="font-size-sm text-muted" :to="{ name: 'user_account_register'}">注册</router-link>
      </div>
      <div class="col-1 ant-divider ant-divider-vertical" role="separator"></div>
      <div class="col-3">
        <a class="font-size-sm text-muted" href="javascript:void(0);">忘记密码</a>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import { useStore } from 'vuex';
import { ref } from 'vue';
import router from '@/router/index';
export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let username = ref('');
    let password = ref('');
    let error_message = ref('');

    const login = () => {
      error_message.value = "";
      store.dispatch("login", {
        username: username.value,
        password: password.value,
        success() {
          store.dispatch("getInfo", {
            success() {
              router.push({ name: 'home' });
            }
          })
        },
        error() {
          error_message.value = "用户名或密码错误";
        }
      });
    }

    return {
      username,
      password,
      error_message,
      login
    }
  }
}
</script>

<style scoped>
.loginform {
  position: fixed;
  top: 20%;
  left: 50%;
  transform: translateX(-50%);
  width: 450px;
  height: 348.398px;
}
.btn_login {
  display: flex;   
  justify-content: center;
  align-items: center;
}
.btn {
    width: 410px;
    height: 36px;
}
span {
  font-size: 1.5em;
}
div.error-message {
  color: red;
}
</style>