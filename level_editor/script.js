// Prevent errors in browsers without FireBug.
if (!window.console)
{
    var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
    "group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];
    window.console = {};
    for (var i = 0; i < names.length; ++i)
        window.console[names[i]] = function() {}
}

// Objects shown in the game for this level.
var gameObjects;

// Objects offered in the palette, they can be dragged on to the level to add a game object.
var paletteObjects;

var PALETTE_OBJECT_NAMES = [
                      "pyramid",
                      "spell",
                      "sphinx",
                      "hazard",
];

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
	gameObjects[0] = new Sprite("sphinx", canvas.width / 2, canvas.height / 2);
	gameObjects[1] = new Sprite("pyramid", canvas.width / 2, 0);

	paletteObjects = new Array();
	var paletteIndex = 0;
	for( var i in PALETTE_OBJECT_NAMES ) {
		console.log(PALETTE_OBJECT_NAMES[i]);
		console.log(paletteIndex);
		paletteObjects[paletteIndex] = new Sprite(
				PALETTE_OBJECT_NAMES[i], 15, 35 + 80 * paletteIndex);
		paletteIndex++;
	}
		
	draw();
}

function draw() {
		
	ctx.drawImage(background, 0, 0);
	
	//Draw game objects.
	for( var i in gameObjects ) {
		var gameObject = gameObjects[i];
		ctx.drawImage(gameObject.image, gameObject.left, gameObject.top);
	}
	
	//Draw palette objects.
	for( var i in paletteObjects ) {
		var paletteObject = paletteObjects[i];
		ctx.drawImage(paletteObject.image, paletteObject.left, paletteObject.top);
	}

	//Draw label for the palette area of the editor.
	ctx.font = "bold x-large sans-serif";
	ctx.fillStyle = "white";
	ctx.textAlign = "left";
	ctx.fillText("Palette", 5, 25);

	//Draw a vertical line separating it.
	ctx.moveTo(90, 0);
	ctx.lineTo(90, 512);
	ctx.lineWidth = 5;
	ctx.strokeStyle = "#000";
	ctx.stroke();

	//Draw label for the play area area of the editor.
	ctx.font = "bold x-large sans-serif";
	ctx.fillStyle = "white";
	ctx.textAlign = "left";
	ctx.fillText("Play Area", 100, 25);
	
	
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
