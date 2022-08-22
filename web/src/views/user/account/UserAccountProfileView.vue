<template>
  <ContentField>
    <div class="container">
      <div class="row">
        <div class="col-3">
          <div class="card" style="margin-top: 20px;">
            <div class="card-body">
              <img :src="$store.state.user.photo" alt="" style="width: 100%;">
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
                <input type="text" class="form-control" id="add-bot-title" placeholder="Please enter the User name">
              </div>
              <div class="mb-3">
                <label for="add-bot-description" class="form-label">个人简介</label>
                <textarea class="form-control" id="add-bot-description" rows="3"
                  placeholder="Please enter User description"></textarea>
              </div>
              <div class="mb-0 btn_update_profile">
                <button type="submit" class="btn btn-block btn-primary font-w400">更新信息</button>
              </div>
            </div>
          </div>
          <div class="card" style="margin-top: 20px;">
            <div class="card-header">
              <span style="font-size: 130%;">个人安全</span>
            </div>
            <div class="modal-body">
              <!-- content -->
              <div class="mb-3">
                <label for="add-bot-title" class="form-label">原密码</label>
                <input type="password" class="form-control" id="password" placeholder="Please enter the password">
              </div>
              <div class="mb-3">
                <label for="add-bot-title" class="form-label">新密码</label>
                <input type="password" class="form-control" id="newPassworde"
                  placeholder="Please enter the new password">
              </div>
              <div class="mb-3">
                <label for="add-bot-title" class="form-label">确认密码</label>
                <input type="password" class="form-control" id="confirmedPassword"
                  placeholder="Please enter the confiredpassword">
              </div>
              <div class="mb-0 btn_update_profile">
                <button type="submit" class="btn btn-block btn-primary font-w400">修改密码</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ContentField>

</template>

<script>
import ContentField from '../../../components/ContentField.vue'
export default {
  components: {
    ContentField
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