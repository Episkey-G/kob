<template>
  <div class="matchround">
    <div class="row">
      <div class="col-6">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="">
        </div>
        <div class="user-username">
          {{$store.state.user.username}}
        </div>
      </div>
      <div class="col-6">
        <div class="user-photo">
          <img :src="$store.state.pk.opponent_photo" alt="">
        </div>
        <div class="user-username">
          {{$store.state.pk.opponent_username}}
        </div>
      </div>
      <div class="col-12" style="text-align:center;padding-top: 10vh;">
        <div class="row-9">
          <button type="button" class="btn btn-warning btn-lg" style="display:inline-block" @click="click_match_btn">{{
            match_btn_info }}</button>
        </div>
        <div class="row-3" v-if="match_btn_info === '取消'">
          <span style=" font-size: 24px;font-weight: 600;color: white;padding-top: 2vh; padding-right: 2vh;">匹配中</span>
          <img src="../assets/images/loading.gif" alt="" style="height:100px; width: 100px;background-size:cover; padding-bottom: 1vh;">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
export default {
  setup() {
    let match_btn_info = ref("开始匹配");
    const store = useStore();
    const click_match_btn = () => {
      if (match_btn_info.value === "开始匹配") {
        match_btn_info.value = "取消";
        // 将信息发送到后端
        store.state.pk.socket.send(JSON.stringify({
          event: "start-matching"
        }));
      } else {
        match_btn_info.value = "开始匹配";
        store.state.pk.socket.send(JSON.stringify({
          event: "stop-matching"
        }));
      }
    }
    return {
      match_btn_info,
      click_match_btn,
    }
  }
}
</script>

<style scoped>
div.matchround {
  width: 60vw;
  height: 70vh;
  margin: 40px auto;
  background-color: rgba(255, 255, 255, 0.33);
  border-radius: 2px 2px 2px 2px;
}
div.user-photo{
  text-align: center; 
  margin-top: 10vh;
}
div.user-photo>img{
  border-radius: 50%;
  width: 20vh;
}
div.user-username{
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: white;
  padding-top: 2vh;
}
</style>
