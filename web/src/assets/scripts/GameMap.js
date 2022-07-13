import { AcGameObject } from "./AcGameObjects";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
  constructor(ctx, parent) {
    super();

    this.ctx = ctx;
    this.parent = parent;
    this.L = 0;

    this.rows = 13;
    this.cols = 13;

    //内部墙的数量
    this.inner_walls_count = 20;
    this.walls = []; //放置墙的数组
  }

  //Flood Fill算法判断是否连通
  check_connectivity(g, sx, sy, tx, ty) {
    if (sx == tx && sy == ty) return true;
    g[sx][sy] = true;

    let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
    for (let i = 0; i < 4; i++) {
      let x = sx + dx[i], y = sy + dy[i];
      if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
        return true;
    }
    return false;
  }


  create_walls() { //创建墙
    const g = []; //判断是否有墙
    for (let r = 0; r < this.rows; r++) {
      g[r] = [];
      for (let c = 0; c < this.cols; c++) {
        g[r][c] = false;
      }
    }

    //给四周加上墙
    //左右两边加上墙
    for (let r = 0; r < this.rows; r++) {
      g[r][0] = g[r][this.cols - 1] = true;
    }
    //上下两行加上墙
    for (let c = 0; c < this.cols; c++) {
      g[0][c] = g[this.rows - 1][c] = true;
    }
    //随机生成轴对称的墙,不重复
    for (let i = 0; i < this.inner_walls_count / 2; i++) {
      let r = Math.floor(Math.random() * this.rows);
      let c = Math.floor(Math.random() * this.cols);
      if (g[r][c] || g[c][r]) {
        i--;
        continue;
      }
      // 不覆盖到左下角和右上角
      if (r == this.rows - 2 && c == 1 || c == this.cols - 2 && r == 1) {
        i--;
        continue;
      }
      g[r][c] = g[c][r] =true;
    }

    //左下角到右上角对角线上没有墙
    // 判断是否连通
    const copy_g = JSON.parse(JSON.stringify(g));
    if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) {
      return false;
    } 

    //生成墙
    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        if (g[r][c]) {
          this.walls.push(new Wall(r, c, this));
        }
      }
    }
    return true;
  }

  start() {
    //随机1000次
    for (let i = 0; i < 1000; i++) {
      if (this.create_walls()) {
        break;
      }
    }
  }

  update_size() {
    // 计算最大的宽高
    this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
    this.ctx.canvas.width = this.L * this.cols;
    this.ctx.canvas.height = this.L * this.rows;
  }

  update() {
    this.update_size();
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