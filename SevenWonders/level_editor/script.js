// The names of the palette objects.
var PALETTE_OBJECT_NAMES = [
                      "pyramid",
                      "spell",
                      "hazard",
                      "protection",
                      "ruby",
                      "scarab",
                      "sphynx",
                      "coin",
];

// The width of the palette area of the canvas. It's on the left. 
// Anything else represents the level.
var PALETTE_WIDTH = 90;

// Size of the level space in an Android level.
var ANDROID_LEVEL_SIZE = 2000;

// Half the size of the level space in an Android level.
var HALF_ANDROID_LEVEL_SIZE = ANDROID_LEVEL_SIZE / 2;

// Size that all images are drawn.
var IMAGE_DRAW_SIZE = 40;

// Size of the canvas used for drawing the game level.
var gameLevelAreaWidth;

// Objects shown in the game for this level.
var gameObjects;

// Object whose location is currently being set by moving the mouse.
var gameObjectBeingMoved;

// Objects offered in the palette, they can be dragged on to the level to add a game object.
var paletteObjects;

// Background image that gets drawn.
var background;

// Canvas element.
var canvas;

// Context of the canvas element.
var ctx;

//Prevent errors in browsers without FireBug.
if (!window.console)
{
    var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
    "group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];
    window.console = {};
    for (var i = 0; i < names.length; ++i)
        window.console[names[i]] = function() {}
}

// Sprite type used for collision detection and drawing. Matches the size of an image.
function Sprite(name, firstLeft, firstTop) {
	
	this.width = IMAGE_DRAW_SIZE;
	this.height = IMAGE_DRAW_SIZE;
	this.name = name;
	
	this.image = new Image();
	this.image.src = name + ".png";
	this.onload = function() {
		draw();
	}
	this.image.onload = this.onload;

	this.move = function(newLeft, newTop) {
		this.left = newLeft;
		this.top = newTop;
		this.right = newLeft + this.width;
		this.bottom = newTop + this.height;
		draw();
	}
	
	this.moveCenter = function(newCenterX, newCenterY) {
		this.move(newCenterX - this.width / 2, newCenterY - this.height / 2);
		draw();
	}
	
	this.drawSprite = function(ctx) {
		ctx.drawImage(this.image, this.left, this.top, this.width, this.height);
	}
	
	this.move(firstLeft, firstTop);
}

function handleLoad() {

	canvas = document.getElementById('gameCanvas');
	gameLevelAreaWidth = canvas.width - PALETTE_WIDTH;
	ctx = canvas.getContext('2d');
	ctx.font = "bold x-large sans-serif";
	ctx.fillStyle = "white";
	ctx.textAlign = "left";
	
	background = new Image();
	background.src = "background.png";
	background.onload = function() {
		draw();
	}
		  
	gameObjects = new Array();

	paletteObjects = new Array();
	var paletteIndex = 0;
	for( var i in PALETTE_OBJECT_NAMES ) {
		console.log(PALETTE_OBJECT_NAMES[i]);
		console.log(paletteIndex);
		paletteObjects[paletteIndex] = new Sprite(
				PALETTE_OBJECT_NAMES[i], 25, 35 + 45 * paletteIndex);
		paletteIndex++;
	}
		
	draw();
}

function draw() {
	
	ctx.fillStyle = "black";
	ctx.fillRect(0, 0, canvas.width, canvas.height);
	
	ctx.drawImage(background, PALETTE_WIDTH, 0, gameLevelAreaWidth, canvas.height);
	
	//Draw game objects.
	for( var i in gameObjects ) {
		var gameObject = gameObjects[i];
		gameObject.drawSprite(ctx);
	}
	
	//Draw palette objects.
	for( var i in paletteObjects ) {
		var paletteObject = paletteObjects[i];
		paletteObject.drawSprite(ctx);
	}

	ctx.fillStyle = "white";
	
	//Draw label for the palette area of the editor.
	ctx.fillText("Palette", 5, 25);

	//Draw a vertical line separating it.
	/*
	ctx.moveTo(90, 0);
	ctx.lineTo(90, 512);
	ctx.lineWidth = 5;
	ctx.strokeStyle = "#000";
	ctx.stroke();
	*/
	
	//Draw label for the play area area of the editor.
	ctx.fillText("Play Area", 100, 25);
	
	
}

function doesSpriteContain(sprite, x, y) {
			
	if ( null == sprite ) {
		return false;
	}
	return (sprite.left <= x &&
			sprite.right >= x &&
			sprite.bottom >= y &&
			sprite.top <= y)
}

function handleClick(e) {

	// If dragging something, clicking drops it.
	if ( null != gameObjectBeingMoved ) {
		gameObjectBeingMoved = null;
		return;
	}

	// Convert mouse coordinates into canvas coordinates.
	var mouseX = e.clientX - canvas.offsetLeft + window.pageXOffset;
	var mouseY = e.clientY - canvas.offsetTop + window.pageYOffset;
	
	// Otherwise, check if we are clicking to pick something up.
	for( var i in gameObjects ) {
		var gameObject = gameObjects[i];
		if ( doesSpriteContain(gameObject, mouseX, mouseY) ) {
			
			console.log("Object grabbed!");

			gameObjectBeingMoved = gameObject;
			gameObjectBeingMoved.moveCenter(mouseX, mouseY);
			break;
		}
	}
	
	for( var i in paletteObjects ) {
		var paletteObject = paletteObjects[i];
		if ( doesSpriteContain(paletteObject, mouseX, mouseY) ) {
			
			console.log("Palette clicked, making new game object!");

			gameObjectBeingMoved = new Sprite(paletteObject.name, 
					paletteObject.left, paletteObject.top,
					paletteObject.drawWidth, paletteObject.drawHeight);
			gameObjectBeingMoved.moveCenter(mouseX, mouseY);
			
			gameObjects[gameObjects.length] = gameObjectBeingMoved;
			
			break;
		}
	}
		
}

function handleMouseOver() {
}

function handleMouseOut() {
}

function handleMouseMove(e) {
	// Convert mouse coordinates into canvas coordinates.
	var mouseX = e.clientX - canvas.offsetLeft + window.pageXOffset;
	var mouseY = e.clientY - canvas.offsetTop + window.pageYOffset;
	
	if ( null != gameObjectBeingMoved ) {
		console.log("Moving object!");
		gameObjectBeingMoved.moveCenter(mouseX, mouseY);
	} else {
		console.log("No grabbed object.");
	}
}

// for now, we are expecting a comma separated list of sprites:
// name, number, number
function parseObjects() {
	var theObjects = document.getElementById('theObjects');
	var theText = theObjects.value;
	
	theText = theText.toLowerCase();
	theText = theText.replace(/ /g, "");
	theText = theText.replace(/;/g, "");
	theText = theText.replace(/this,/g, "");
	theText = theText.replace(/\t/g, "");
	theText = theText.replace(/\)/g, "");
	theText = theText.replace(/\(/g, ",");
	theText = theText.replace(/^add/g, "");
	theText = theText.replace(/\nadd/g, "\n");
	
	var lines = theText.split("\n");
	gameObjects = new Array();
	for (var i = 0;i < lines.length;++i) {
		parseLine(lines[i]);
	}
	draw();
}

function convertAndroidXToCanvasX(androidX) {
	// Convert android x to always be positive, then fit entire width of game level area
	// into the space in the canvas for the game level.
	return ((androidX + HALF_ANDROID_LEVEL_SIZE) * gameLevelAreaWidth / ANDROID_LEVEL_SIZE) + PALETTE_WIDTH;
}

function convertAndroidZToCanvasY(androidZ) {
	return (androidZ + HALF_ANDROID_LEVEL_SIZE) * canvas.height / ANDROID_LEVEL_SIZE;
}

function parseLine(line) {
	var params = line.split(",");
	if(params.length == 3 && ! isNaN(parseInt(params[1])) && ! isNaN(parseInt(params[2]))) {
		var name = params[0];
		var x = convertAndroidXToCanvasX(parseInt(params[1]));
		var y = convertAndroidZToCanvasY(parseInt(params[2]));

		console.log("Reading in from level code:" + name + ", " + x + ", " + y);
		
		gameObjects[gameObjects.length] = new Sprite(name,x,y);
	}
}

