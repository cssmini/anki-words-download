var fetch = require("node-fetch");
var fs = require("fs");
var readline = require('readline');
// 文件保存路径
var filePath = 'file'
var errWord = new Array();

readFileToArr('reader.txt', function(data) {
    console.log('>>>>>>>>>>>>>>>>> download start');
    console.log('>>>>>>>>>>>>>>>>> 单词数量：' + data.length + '');
    for (let i = 0; i < data.length; i++) {
        let word = data[i];
        let url = 'http://media.shanbay.com/audio/us/' + word + '.mp3';
        download(url, url.split("/").reverse()[0]);
        console.log(url);
    }
    console.log('>>>>>>>>>>>>>>>>> done');

});


/*
 * 按行读取文件内容
 * 返回：字符串数组
 * 参数：fReadName:文件名路径
 *      callback:回调函数
 * */
function readFileToArr(fReadName, callback) {
    console.log('>>>>>>>>>>>>>>>>> loading file data');
    var fRead = fs.createReadStream(fReadName);
    var objReadline = readline.createInterface({
        input: fRead
    });
    var arr = new Array();
    var index = 0;
    objReadline.on('line', function(line) {
        index++;
        console.log('line' + index + ' : ' + line);
        arr.push(line);
    });
    objReadline.on('close', function() {
        // console.log(arr);
        callback(arr);
    });
    console.log('>>>>>>>>>>>>>>>>> done');
}
/**
 *  下载文件
 */
function download(u, p) {
    return fetch(u, {
        method: 'GET',
        headers: { 'Content-Type': 'application/octet-stream' },
    }).then(res => res.buffer()).then(_ => {
        fs.writeFile(filePath + '/' + p, _, "binary", function(err) {
            console.log(err || p);
        });
    }).catch(err => {
        errWord.push({ 'url': u, 'fileName': p });
        console.log('单词文件：', p);
        console.log('报错了..')
    });
}