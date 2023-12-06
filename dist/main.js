
/** @type {[number, number][][]} */
let frames;

/**
 * @param {Uint8Array} arr 
 */
function init(arr){
    frames = [];
    let contours = [];
    let contour = [];
    for(let i = 0; i < arr.length;){
        let byte = arr[i++];
        
        if(byte === 0xff){
            frames.push(contours);
            contours = [];
        }else if((byte & 0xfc) === 0xfc){
            contours.push([byte === 0xfd, contour]);
            contour = [];
        }else {
            let num = (byte << 8) | arr[i++];
            let x = num % 240, y = Math.floor(num / 240)
            contour.push([x, y]);
        }
    }
}

/** @type {HTMLCanvasElement} */
const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d", { alpha: false });
canvas.width = 480; canvas.height = 360;

function drawFrame(frame){
    ctx.fillStyle = "#111";
    ctx.fillRect(0, 0, 480, 360);
    for(const [isWhite, contour] of frame){
        ctx.beginPath();
        let isFirst = true;
        for(const point of contour){
            ctx[isFirst ? "moveTo" : "lineTo"](point[0] * 2, point[1] * 2);
            isFirst = false;
        }
        ctx.closePath();
        ctx.fillStyle = isWhite ? "#eee" : "#111";
        ctx.fill();
    }
}

let timer = 0;
let prevTime;

let frameRate = 12;

let currFrame = 0;
function draw(time){
    const deltaTime = time - prevTime;
    timer += deltaTime;
    if(timer > 1000/frameRate){
        timer %= 1000/frameRate;
        if(currFrame < frames.length){
            drawFrame(frames[currFrame++]);
        }
    }
    requestAnimationFrame(draw);
    prevTime = time;
}


fetch("./badapple.bin")
    .then((res) => res.arrayBuffer())
    .then((buffer) => {
        init(new Uint8Array(buffer));   
        prevTime = performance.now(); 
        requestAnimationFrame(draw);
        document.getElementById("loading").remove();
    });