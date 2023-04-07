<template>
  <ContentField class="loginform">
    <div class="row justify-content-md-center">
      <div class="col-12">
        <form @submit.prevent="register">
          <div class="mb-3 text-center">
            <span class="text-dark font-size-h1">用户注册</span>
          </div>
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="username">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="password">
          </div>
          <div class="mb-3">
            <label for="confirmedPassword" class="form-label">确认密码</label>
            <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword"
              placeholder="confirmedPassword">
          </div>
          <div class="error-message">{{error_message}}</div>
          <div class="mb-0 btn_login">
            <button type="submit" class="btn btn-block btn-primary font-w400">注册</button>
          </div>
        </form>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import { ref } from 'vue';
import $ from 'jquery';
import router from '@/router/index';
export default {
  components: {
    ContentField
  },
  setup() {
    let username = ref('');
    let password = ref('');
    let confirmedPassword = ref('');
    let error_message = ref('');

    const register = () => {
      error_message.value = "";
      $.ajax({
        url: "http://127.0.0.1:3000/user/account/register/",
        type: "POST",
        data: {
          username: username.value,
          password: password.value,
          confirmedPassword: confirmedPassword.value
        },
        success(resp) {
          if (resp.error_message === "success") {
            alert("注册成功！");
            router.push({name: "user_account_login"});
          } else {
            error_message.value = resp.error_message;
          }
        },
      });
    }

    return {
      username,
      password,
      confirmedPassword,
      error_message,
      register
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