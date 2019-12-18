音频下载地址：
```javascript

// 男声
https://ssl.gstatic.com/dictionary/static/sounds/oxford/{0}--_gb_1.mp3

// 女声
http://media.shanbay.com/audio/us/{0}.mp3

```

格式：
```javascript
单词|音标|释义|发音|例句|例句翻译|图片
```

格式例子：
```javascript
form|[fɔrm]|n. 形式，形状；形态，外形；方式；表格<br>vt. 构成，组成；排列，组织；产生，塑造<br>vi. 形成，构成；排列<br>n. (Form)人名；(英)福姆；(法、德)福尔姆|[sound:dog.mp3]|i have a dog|我有一只狗|<img src="3dd762165aca4556a12d4e429920bde1.jpg">

```

#### 步骤：

1. 确认要制作卡片的单词

2. 在 有道云笔记 中保存单词到单词本中

3. 在 有道云笔记导出单词()

4. 转换~~两种~~单词格式为 txt 文件；(~~单列只有英语单词的格式~~ | 单词、音标、释义都有的格式)

5. 使用转换器获取上述所说的~~两种~~格式

6. 运行 java 程序下载单词图片、音频文件

 > 1. 输入 anki 缓存图片的路径，自动保存图片、音频到该路径
 
 > 2. 为防止图片、音频重复，要求用户输入文件自定义前缀


7. 运行 java 程序 生成 txt 文件

> 格式: 单词|音标|释义|发音|例句|例句翻译|图片
 
8. 导入文件 anki

9. end