var winWidth = window.screen.width;
var size = (winWidth / 750) * 100;
var rem =size
document.documentElement.style.fontSize = (size < 100 ? size : 100) + 'px';