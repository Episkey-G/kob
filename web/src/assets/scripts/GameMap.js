import { AcGameObject } from "./AcGameObjects";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
  constructor(ctx, parent, store) {
    super();

    this.ctx = ctx;
    this.parent = parent;
    this.store = store;
    this.L = 0;

    this.rows = 13;
    this.cols = 14;

    //内部墙的数量
    this.inner_walls_count = 20;
    this.walls = []; //放置墙的数组

    this.snakes = [
      new Snake({ id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
      new Snake({ id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this)
    ];
  }

  create_walls() { //创建墙
    const g = this.store.state.pk.gamemap;

    //生成墙
    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        if (g[r][c]) {
          this.walls.push(new Wall(r, c, this));
        }
      }
    }
  }

  add_listening_events() {
    this.ctx.canvas.focus();

    this.ctx.canvas.addEventListener("keydown", e => {
      let d = -1;
      if (e.key === 'w') d = 0;
      else if (e.key === 'd') d = 1;
      else if (e.key === 's') d = 2;
      else if (e.key === 'a') d = 3;

      if (d >= 0) {
        this.store.state.pk.socket.send(JSON.stringify({
          event: "move",
          direction: d,
        }));
      }
    });
  }

  start() {
    this.create_walls();
    this.add_listening_events();
  }

  update_size() {
    // 计算最大的宽高
    this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
    this.ctx.canvas.width = this.L * this.cols;
    this.ctx.canvas.height = this.L * this.rows;
  }

  // 判断两条蛇是否准备好进入下一回合
  check_ready() { 
    for (const snake of this.snakes) {
      if (snake.status !== "idle") return false;
      if (snake.direction === -1) return false;
    }
    return true;
  }

  next_step() {
    for (const snake of this.snakes) {
      snake.next_step();
    }
  }

  // 检测目标位置是否合法:没有撞到蛇的身体和墙
  check_valid(cell) {
    // 遍历墙
    for (const wall of this.walls) {
      if (wall.r === cell.r && wall.c === cell.c) {
        return false;
      }
    }

    // 遍历蛇
    for (const snake of this.snakes) {
      let k = snake.cells.length;
      // 当蛇尾移动时，无需判断
      if (!snake.check_tail_increasing()) {
        k --;
      }
      for (let i = 0; i < k; i ++) {
        if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) {
          return false;
        }
      }
    }
    return true;
  }

  update() {
    this.update_size();
    if (this.check_ready()) {
      this.next_step();
    }
    this.render();
  }

  render() {
    const color_even = "#B4D563", color_odd = "#ACCF5C";
    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        if ((r + c) % 2 == 0) {
          this.ctx.fillStyle = color_even;
        } else {
          this.ctx.fillStyle = color_odd;
        }
        this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
      }
    }
  }
}