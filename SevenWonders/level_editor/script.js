var testSprite;

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

	background = document.getElementById('background');
	test1 = document.getElementById('test1');
	test2 = document.getElementById('test2');

	testSprite = new Sprite(test1, canvas.width / 2, canvas.height / 2);
		
	draw();
}

function draw() {
		
	ctx.drawImage(background, 0, 0);

	ctx.drawImage(testSprite.image, testSprite.left, testSprite.top);

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
