import { AcGameObject } from "./AcGameObjects";
import { Cell } from "./Cell";

export  class Snake extends AcGameObject {
  constructor(info, gamemap) {
    super();

    this.id = info.id;
    this.color = info.color;
    this.gamemap = gamemap;

    this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体,cells[0]存放蛇头
    this.next_cell = null; // 存放下一步的目标位置

    this.speed = 5; // 蛇每秒走的格子数
    this.direction = -1; // -1表示没有指令，0、1、2、3表示上右下左
    this.status = "idle"; // idle表示静止，move表示移动，die表示死亡

    this.dr = [-1, 0, 1, 0] // 上右下左 行的偏移量
    this.dc = [0, 1, 0, -1] // 上右下左 列的偏移量

    this.step = 0; // 蛇移动的回合数
    this.eps = 1e-2; // 允许的误差

    this.eye_direction = 0;
    // 左下角的蛇初使朝上，右上角的蛇初使朝下
    if (this.id === 1) this.eye_direction = 2;

    // 蛇眼睛的偏移量
    this.eye_dx = [
      [-1, 1],
      [1, 1],
      [1, -1],
      [-1, -1]
    ];
    this.eye_dy = [
      [-1, -1],
      [-1, 1],
      [1, 1],
      [1, -1]
    ];
  }

  start() {

  }

  set_direction(d) {
    this.direction = d;
  }

  // 检测当前回合蛇的长度是否增加
  /**
   * 前10回合都增加
   * 10回合之后，每三回合增加一格长度
   */
  check_tail_increasing() {
    if (this.step <= 10) return true;
    return this.step % 3 === 1;
  }

  next_step() { // 将蛇的状态改为移动，走下一步
    const d = this.direction;
    this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
    this.eye_direction = d;
    this.direction = -1; // 清空操作
    this.status = "move"; // 改变状态
    this.step ++;

    // 蛇移动增加长度
    const k = this.cells.length;
    for (let i = k; i > 0; i --) {
      this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
    }

    // 蛇如果死亡，则无法进行下一步操作
    if (!this.gamemap.check_valid(this.next_cell)) {
      this.status = "die";
    }
  }

  update_move() {
    const dx = this.next_cell.x - this.cells[0].x; // x方向的偏移量
    const dy = this.next_cell.y - this.cells[0].y; // y方向的偏移量
    const distance = Math.sqrt(dx * dx + dy * dy); // 两点之间的距离

    // 判断是否抵达目标位置
    if (distance < this.eps) {
      this.cells[0] = this.next_cell; // 更新蛇头
      this.next_cell = null; //清空操作
      this.status = "idle"; //走完变为静止

      // 蛇不变长时移动，蛇头增加一格，蛇尾减去一格
      if (!this.check_tail_increasing()) {
        this.cells.pop();
      }
    } else {
      const move_distance = this.speed * this.timedelta / 1000; // 每两帧之间走过的距离
      this.cells[0].x += move_distance * dx / distance;
      this.cells[0].y += move_distance * dy / distance; 

      // 蛇不变长时移动，蛇头增加一格，蛇尾减去一格
      if (!this.check_tail_increasing()) {
        const k = this.cells.length;
        const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
        const tail_dx = tail_target.x - tail.x;
        const tail_dy = tail_target.y - tail.y;
        tail.x += move_distance * tail_dx / distance;
        tail.y += move_distance * tail_dy / distance;
      }
    }
  }

  // 每一帧执行一次
  update() {
    if (this.status === "move") {
      this.update_move();
    }
    this.render();
  }

  render() {
    const L = this.gamemap.L;
    const ctx = this.gamemap.ctx;

    ctx.fillStyle = this.color;
    // 当蛇死亡时变成白色
    if (this.status === "die") {
      ctx.fillStyle = "white";
    }
    // 遍历蛇的身体
    for (const cell of this.cells) {
      ctx.beginPath();
      ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
      ctx.fill();
    }

    // 使用矩形填充蛇的身体
    for (let i = 1; i < this.cells.length; i ++) {
      const a = this.cells[i], b = this.cells[i - 1];
      if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
        continue;
      }
      if (Math.abs(a.x - b.x) < this.eps) {
        ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
      } else {
        ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
      }
    }

    ctx.fillStyle = "black";
    for (let i = 0; i < 2; i ++) {
      const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.2) * L;
      const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.2) * L;
      ctx.beginPath();
      ctx.arc(eye_x, eye_y, L * 0.06, 0, Math.PI * 2);
      ctx.fill();
    }
    
  }
}