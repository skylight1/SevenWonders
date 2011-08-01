
var gameObjects;

var background;
var canvas;
var ctx;

// Sprite type used for collision detection and drawing. Matches the size of an image.
function Sprite(name, firstLeft, firstTop) {
	
	this.image = new Image();
	this.image.src = name + ".png";
	this.image.onload = function() {
		draw();
	}

	this.move = function(newLeft, newTop) {
		this.left = newLeft;
		this.top = newTop;
		this.right = newLeft + this.image.width;
		this.bottom = newTop + this.image.height;
	}
	this.move(firstLeft, firstTop);
}

function handleLoad() {

	canvas = document.getElementById('gameCanvas');
	ctx = canvas.getContext('2d');
	background = new Image();
	background.src = "background.png";
	background.onload = function() {
		draw();
	}
		  
	gameObjects = new Array();
	gameObjects[0] = new Sprite("test1", canvas.width / 2, canvas.height / 2);
	gameObjects[1] = new Sprite("test2", canvas.width / 2, 0);
		
	draw();
}

function draw() {
		
	ctx.drawImage(background, 0, 0);
	
	for( var i in gameObjects ) {
		var gameObject = gameObjects[i];
		ctx.drawImage(gameObject.image, gameObject.left, gameObject.top);
	}

}

function collisionCheckSprites(a, b) {
	if ( null == a || null == b ) {
		return false;
	}
	return (a.left <= b.right &&
		b.left <= a.right &&
		a.top <= b.bottom &&
		b.top <= a.bottom)
}

function handleClick() {
}

function handleMouseOver() {
}

function handleMouseOut() {
}

function handleMouseMove(e) {
}
