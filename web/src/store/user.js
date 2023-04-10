import $ from 'jquery';

export default {
  state: {
    id: "",
    username: "",
    description: "",
    photo: "",
    token: "",
    is_login: false,
    pulling_info: true,
  },
  getters: {
  },
  mutations: {
    updateUser(state, user) {
      state.id = user.id;
      state.username = user.username;
      state.photo = user.photo;
      state.is_login = user.is_login;
    },
    updateToken(state, token) {
      state.token = token;
    },
    logout(state) {
      state.id = "";
      state.username = "";
      state.photo = "";
      state.token = "";
      state.is_login = false;
    },
    updatePullingInfo(state, pulling_info) {
      state.pulling_info = pulling_info;
    }
  },
  actions: {
    login(context, data) {
      $.ajax({
        url: "https://app2800.acapp.acwing.com.cn/api/user/account/token/",
        type: "POST",
        data: {
          username: data.username,
          password: data.password
        },
        success(resp) {
          if (resp.error_message === "success") {
            localStorage.setItem("jwt_token", resp.token);
            context.commit("updateToken", resp.token);
            data.success(resp);
          } else {
            data.error(resp);
          }
        },
        error(resp) {
          data.error(resp);
        }
      });
    },
    getInfo(context, data) {
      $.ajax({
        url: "https://app2800.acapp.acwing.com.cn/api/user/account/info/",
        type: "GET",
        headers: {
          Authorization: "Bearer " + context.state.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            context.commit("updateUser", {
              ...resp,
              is_login: true
            });
            data.success(resp);
          } else {
            data.error(resp);
          }
        },
        error(resp) {
          data.error(resp);
        }
      });
    },
    logout(context) {
      localStorage.removeItem("jwt_token");
      context.commit("logout");
    },
    updatePullingInfo(context) {
      context.commit("updatePullingInfo");
    },
    update_password(context, data) {
            $.ajax({
                type: "POST",
              url: "https://app2800.acapp.acwing.com.cn/api/user/account/update/password/",
                data: {
                    oldPassword: data.old_password,
                    newPassword: data.new_password,
                    confirmPassword: data.confirm_password,
                },
                headers: {
                    authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateToken", resp.token);
                        localStorage.setItem("jwt_token", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                },
            });
        }
  },
  modules: {
  }
}