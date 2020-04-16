/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function divideEqually() {
    const ul = $("ul.divide-equally");
    const li = ul.children("li");
    const num = li.length;
    const width = Math.floor(100 / num);
    const margin = (100 % num) / num / 2;

    ul.css("padding", 0);
    ul.css("width", "100%");

    li.css("display", "table-cell");
    li.css("width", width+"%");
    li.css("margin-right", margin+"%");
    li.css("margin-left", margin+"%");
    li.children("*").css("width", "100%")
}

$(function () {
    divideEqually();
});
