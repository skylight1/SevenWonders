
var GAME_OBJECTS = [
	"test1", 
	"test2", 
];

var testSprite1;
var testSprite2;

// Images
var background;
var test1;
var test2;

// Resources	
var canvas;
var ctx;

// Sprite type used for collision detection and drawing. Matches the size of an image.
function Sprite(image, firstLeft, firstTop) {
	this.image = image;
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
	
	test1 = new Image();
	test1.src = "test1.png";
	test1.onload = function() {
		draw();
	}	

	test2 = new Image();
	test2.src = "test2.png";	  
	test2.onload = function() {
		draw();
	}	

	  
	testSprite1 = new Sprite(test1, canvas.width / 2, canvas.height / 2);
	testSprite2 = new Sprite(test1, canvas.width / 2, 0);
		
	draw();
}

function draw() {
		
	ctx.drawImage(background, 0, 0);

	ctx.drawImage(testSprite1.image, testSprite1.left, testSprite1.top);
	
	ctx.drawImage(testSprite2.image, testSprite2.left, testSprite2.top);

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
