<template>
  <ContentField>
    <div class="container">
      <div class="row">
        <div class="col-3">
          <div class="card" style="margin-top: 20px; text-align: center;">
            <div class="card-body">
              <img :src="$store.state.user.photo" alt="" style="width: 100%;">
              <button class="btn btn-primary btn-block mt-3">上传图片</button>
            </div>
          </div>
        </div>
        <div class="col-9">
          <div class="card" style="margin-top: 20px;">
            <div class="card-header">
              <span style="font-size: 130%;">个人信息</span>
            </div>
            <div class="modal-body">
              <!-- content -->
              <div class="mb-3">
                <label for="add-bot-title" class="form-label">用户名</label>
                <input v-model="username" type="text" class="form-control" id="add-bot-title"
                  placeholder="Please enter the User name">
              </div>
              <div class="mb-3">
                <label for="add-bot-description" class="form-label">个人简介</label>
                <textarea v-model="description" class="form-control" id="add-bot-description" rows="3"
                  placeholder="Please enter User description"></textarea>
              </div>
              <div class="mb-0 btn_update_profile">
                <button type="submit" class="btn btn-block btn-primary font-w400" @click="update_user"
                  data-bs-toggle="modal" data-bs-target="#updateInfo">
                  更新信息
                </button>
              </div>
            </div>


            <!-- Modal -->
            <div class="modal fade" id="updateInfo" tabindex="-1">
              <div class="modal-dialog modal-sm">
                <div class="modal-content">
                  <div class="modal-header">
                    <div class="modal-title">提示</div>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div v-if="isSuccess" class="modal-body mx-auto">
                    已经成功更新你的资料
                  </div>
                  <div v-else class="modal-body mx-auto">
                    <span class="error_message">{{ error_message }}</span>
                  </div>
                  <div class="modal-footer mx-auto">

                    <button type="button" :class="isSuccess ? 'btn btn-success btn-sm' : 'btn btn-danger btn-sm'"
                      style="width: 60px;" data-bs-dismiss="modal">确认</button>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div class="card">
            <div class="card-body">
              <h3>个人安全</h3>
              <hr>
              <div class="row user-security">
                <div class="col-sm-4">
                  <img src="https://cdn.acwing.com/static/web/img/user/profile/security/password.jpg"
                    style="width: 25px; margin-left:10px;" alt="">

                  <span>密码</span>
                </div>
                <div class="col-sm-4">
                  <span>已设置</span>
                </div>
                <div class="col-sm-4 mx-auto">
                  <!-- Button trigger modal -->
                  <button @click="update_message" type="button" class="btn btn-link" style="text-decoration:none"
                    data-bs-toggle="modal" data-bs-target="#modifyPassword">修改密码</button>
                </div>
                <!-- Modal -->
                <div class="modal fade" id="modifyPassword" tabindex="-1">
                  <div class="modal-dialog">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title">修改密码</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>

                      <div class="modal-body">
                        <form>
                          <div class="mb-3">
                            <label for="old_password" class="form-label">原密码</label>
                            <input v-model="updatePassword.old_password" type="password" class="form-control"
                              id="old_password">
                          </div>
                          <div class="mb-3">
                            <label for="new_password" class="form-label">新密码</label>
                            <input v-model="updatePassword.new_password" type="password" class="form-control"
                              id="new_password">
                          </div>
                          <div class="mb-3">
                            <label for="confirm_password" class="form-label">确认密码</label>
                            <input v-model="updatePassword.confirm_password" type="password" class="form-control"
                              id="confirm_password">
                          </div>
                        </form>
                      </div>
                      <div class="modal-footer">
                        <span class="error_message">{{ error_message }}</span>
                        <button @click="update_password" type="button" class="btn btn-primary">确认</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import $ from 'jquery';
import { reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap';

export default {
  name: " UserInfoView ",
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let description = ref("");
    let username = ref("");
    let error_message = ref("");
    let updatePassword = reactive({
      old_password: "",
      new_password: "",
      confirm_password: "",
    });

    let isSuccess = ref(false);

    const user_info = () => {
      $.ajax({
        type: "GET",
        url: "https://app2800.acapp.acwing.com.cn/api/user/account/info/",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          description.value = resp.description;
          username.value = resp.username;
        }
      });
    };

    const update_user = () => {
      error_message.value = "";
      $.ajax({
        type: "POST",
        url: "https://app2800.acapp.acwing.com.cn/api/user/account/updateInfo/",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          username: username.value,
          description: description.value
        },
        success(resp) {
          if (resp.error_message === "success") {
            isSuccess.value = true;
            store.state.user.username = username.value;
            store.state.user.description = description.value;
          } else {
            isSuccess.value = false;
            username.value = store.state.user.username;
            description.value = store.state.user.description;
            error_message.value = resp.error_message;
          }
        }
      });
    }

    const update_password = () => {
      error_message.value = "";
      store.dispatch("update_password", {
        ...updatePassword,
        success() {
          Modal.getInstance("#modifyPassword").hide();
          isSuccess.value = true;
          alert("修改成功!");
        },
        error(resp) {
          error_message.value = resp.error_message;
          alert("修改失败!");
        }
      })
    }
    const update_message = () => {
      error_message.value = "";
    }

    user_info();

    return {
      description,
      username,
      updatePassword,
      error_message,
      isSuccess,
      update_user,
      user_info,
      update_password,
      update_message,
    }
  }

}
</script>

<style scoped>
.btn_update_profile {
  display: flex;
  justify-content: center;
  align-items: center;
}

.container {
  margin-top: 20px;
}
</style>