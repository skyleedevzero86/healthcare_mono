'use strict';

const gulp = require('gulp');

const	ejs = require("gulp-ejs");
const	rename = require('gulp-rename');

const sass = require('gulp-sass');
const	sourcemaps = require('gulp-sourcemaps');

const	spritesmith = require('gulp.spritesmith');

const fs = require("fs");
var sequence = require('run-sequence');
const clean = require('gulp-clean');

/* ejs */
gulp.task('gulpEjs', function() {
	return gulp
	.src(["./ejs/**/*.ejs", "!" + "./ejs/_include/*"])
	.pipe(ejs())
	.pipe(rename({ extname: '.html' }))
	.pipe(gulp.dest("./html/"));
});
gulp.task('gulpEjs:watch', function(){
 	gulp.watch('./ejs/**/*.ejs', ['gulpEjs']);
});

/* sass & map 
	outputStyle: nested
	outputStyle: expanded
	outputStyle: compact
	outputStyle: compressed
*/
gulp.task('sass', function () {
	return gulp
	.src('./scss/style.scss')
	.pipe(sourcemaps.init())
	.pipe(sass.sync({outputStyle: 'expanded'}).on('error', sass.logError))
	.pipe(sourcemaps.write('./'))
	.pipe(gulp.dest('./css'));
});
gulp.task('sass:watch', function(){
	gulp.watch('./scss/**/*.scss', ['sass']);
});

/* sprite */
gulp.task('sprite', function(){
	var spriteData = gulp
	.src('./images/sprite_in/*.png')
	.pipe(spritesmith({
		imgName: 'sprite_all.png',
		padding: 4,
		cssName: '_sprite_all.scss',
		imgPath: '../images/sprite_out/sprite_all.png'
	}));
	spriteData.img.pipe(gulp.dest('./images/sprite_out'));
	spriteData.css.pipe(gulp.dest('./scss'));
});
gulp.task('sprite:watch', function(){
	gulp.watch('./images/sprite_in/*.png', ['sprite']);
});

/* front html list */
const inspectionFindFile = (destPath) => {
	try {    
		 fs.readdirSync(destPath, { withFileTypes: true })
				.forEach((file, i) => {
					 const path = `${destPath}/${file.name}`;
				
					 if (file.isDirectory()) {
						const appendFolder = `
		<h2 class="front_html_h2">${file.name}</h2>`;
						 fs.appendFileSync('퍼블_작업리스트.html', appendFolder, (err) => {
							 if (err) console.log('Error: ', err);
							 else console.log('File created');
						 });
						
						 inspectionFindFile(path);
							
					 } else {
						 // 파일 처리 파일은 따로 배열에 담아 처리하시면됩니다.

						 const appendList = `
		<a href="${path}" class="front_html_link">sub_${i}_${file.name}</a>`;

							fs.appendFileSync('퍼블_작업리스트.html', appendList, (err) => {
								if (err) console.log('Error: ', err);
								else console.log('File created');
							});
					 }
				});
	 } catch(err) {
		 return console.error('Read Error', err);
	 }
}

gulp.task('html_list', function(){
	const htmlTagFirst = `<!DOCTYPE html>
	<html lang="ko">
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<style>
			body{
				padding-left: 20px;
			}
			.front_html_h1,
			.front_html_h2{
				margin: 0;
				padding: 0;
			}
			.front_html_h1{
				font-size: 22px;
				color: #000;
				margin: 20px 0 10px;
			}
			.front_html_h2{
				font-size: 18px;
				color: #333;
				margin-top: 10px;
			}
			.front_html_link{
				display: block;
				font-size: 14px;
				font-weight: 700;
				padding: 5px 10px;
				color: #555;
				text-decoration:none;
			}
			.front_html_link:hover{
				background-color: #76a9ec;
				color: #fff;
			}
		</style>

		<title>퍼블 작업리스트</title>
	</head>
	<body>
		<h1 class="front_html_h1">퍼블 작업리스트</h1>`;

	const htmlTagLast = `
	</body>
	</html>`;
	
	fs.writeFileSync('퍼블_작업리스트.html', htmlTagFirst, (err) => {});
	inspectionFindFile("html");
	fs.appendFile('퍼블_작업리스트.html', htmlTagLast, (err) => {});
});

/* help */
gulp.task('help', function(){
	console.log("");
	console.log(" [ HELP ] ");
	console.log("┏━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
	console.log("┃$ gulp help         ┃ help                         ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp ejs          ┃ gulp ejs include             ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp ejs:watch    ┃ gulp ejs include : watch     ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp sass         ┃ sass > css                   ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp sass:watch   ┃ sass > css : watch           ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp sprite       ┃ *.png > all.png + scss       ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp sprite:watch ┃ *.png > all.png + scss watch ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp first        ┃ all(ejs, sass, sprite)       ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp watch        ┃ watch(ejs, sass, sprite)     ┃");
	console.log("┗━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
	console.log("");
});

/* help */
gulp.task('info', function(){
	console.log("");
	console.log(" [ INFO ] ");
	console.log("┏━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
	console.log("┃$ gulp help         ┃ help                         ┃");
	console.log("┣━━━━━━━━━━━━━━━━━━━━╋━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
	console.log("┃$ gulp watch        ┃ watch(ejs, sass, sprite)     ┃");
	console.log("┗━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
	console.log("");
});

/* default */
gulp.task('default', ['info']);

gulp.task('clean', () => {
	return gulp
		.src('html/*', {read: false})
    .pipe(clean());
});

gulp.task('ejsWatch', (callback) => {
	sequence(
		'clean',
		'gulpEjs',
		'html_list',
		callback
	);
});

/* first */
gulp.task('first', function(callback){
	sequence(
		'ejsWatch',
		['sass', 'sprite'],
		callback
	);
});

/* watch */
gulp.task('watch', function() {
	gulp.start(['first']);
	gulp.watch(['./ejs/**/*.ejs'], ['ejsWatch']);
	gulp.watch(['./scss/**/*.scss'], ['sass']);
	gulp.watch(['./images/sprite_in/*.png'], ['sprite']);
});