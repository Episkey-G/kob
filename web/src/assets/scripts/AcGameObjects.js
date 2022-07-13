const AC_GAME_OBJECTS = [];

export class AcGameObject {
  constructor() {
    AC_GAME_OBJECTS.push(this);
    //时间间隔
    this.timedelta = 0;
    this.has_called_start = false; //是否已经调用了start方法
  }

  start() { //只执行一次

  }

  update() { //每帧执行一次,除了第一帧之外

  }

  on_destroy() { //每次销毁时执行一次

  }

  destroy() {
    this.on_destroy();

    for (let i in AC_GAME_OBJECTS) {
      const obj = AC_GAME_OBJECTS[i];
      if (obj === this) {
        AC_GAME_OBJECTS.splice(i, 1);
        break;
      }
    }
  }

}

//上一次执行的时刻
let last_timestamp;

const setp = timestamp => {
  for (let obj of AC_GAME_OBJECTS) {
    if (!obj.has_called_start) {
      obj.has_called_start = true;
      obj.start();
    } else {
      obj.timedelta = timestamp - last_timestamp;
      obj.update();
    }

  }

  last_timestamp = timestamp;
  requestAnimationFrame(setp);

}

requestAnimationFrame(setp);
