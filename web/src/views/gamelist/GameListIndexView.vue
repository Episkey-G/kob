<template>
  <div class="col-md-9">
    <div class="card" style="margin-top: 20px;">
      <div class="card-header">
        <span style="font-size: 130%;">Game</span>
        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-game-btn">
          Create Game
        </button>
        <!-- Modal -->
        <div class="modal fade" id="add-game-btn" tabindex="-1">
          <div class="modal-dialog modal-xl">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Create Game</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <!-- content -->
                <div class="mb-3">
                  <label for="add-game-title" class="form-label">Game Title</label>
                  <input type="text" class="form-control" id="add-game-title" placeholder="Please enter the Game name">
                </div>
                <div class="mb-3">
                  <label for="add-game-description" class="form-label">Game Description</label>
                  <textarea class="form-control" id="add-game-description" rows="3"
                    placeholder="Please enter Game description"></textarea>
                </div>
                <div class="mb-3">
                  <label for="add-game-code" class="form-label">Game Link</label>
                  <input type="text" class="form-control" id="add-game-title" placeholder="Please enter the Game link">
                </div>
              </div>
              <div class="modal-footer">
                <div class="error-message"></div>
                <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="card-body">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th>游戏名称</th>
              <th>简介</th>
              <th>作者</th>
              <th>创建时间</th>
              <th>修改时间</th>

            </tr>
          </thead>
          <tbody>
            <tr v-for="game in games" :key="game.id">
              <td>
                <router-link class="game_name" :to="{ name: 'pk_index'}">
                  {{game.name}}
                </router-link>
              </td>
              <td class="hideCol" data-toggle="tooltip" data-placement="top" :title="game.description">
                {{game.description}}</td>
              <td>{{ arr[game.id] }}</td>
              <td>{{game.createTime}}</td>
              <td>{{game.modifyTime}}</td>

            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { ref,reactive } from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();
    let games = ref([]);
    let users = ref([]);
    let arr = reactive([]);
    const refresh_bots = () => {
      $.ajax({
        url: "https://app2800.acapp.acwing.com.cn/api/user/game/getGameList/",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          games.value = resp;
          for (let i = 0; i < JSON.parse(JSON.stringify(games.value.length)); i ++) {
            $.ajax({
              url: "https://app2800.acapp.acwing.com.cn/api/user/account/username/",
              type: "GET",
              data: {
                user_id: JSON.parse(JSON.stringify(games.value))[i].userId
              },
              headers: {
                Authorization: "Bearer " + store.state.user.token,
              },
              success(resp) {
                users.value = resp.username;
                arr[i+1] = users.value;
              }
            });
          }
        }
      });
    }
    refresh_bots();

    return {
      games,
      users,
      arr
    }
  }


}
</script>

<style scoped>
div.col-md-9 {
  display: block;
  margin: 0 auto;
}
table {
  table-layout: fixed;
}
td.hideCol {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
td {
  font-size: 15px;
}
.game_name {
  text-decoration: none;
  color: #447AB2;
}
</style>