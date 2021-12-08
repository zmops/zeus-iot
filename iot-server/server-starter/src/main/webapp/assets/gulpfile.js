
/*!
 layuiAdmin pro 构建
*/

var pkg = require('./package.json');
var inds = pkg.independents;

var gulp = require('gulp');
var uglify = require('gulp-uglify');
var minify = require('gulp-minify-css');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var replace = require('gulp-replace');
var header = require('gulp-header');
var del = require('del');
var gulpif = require('gulp-if');
var minimist = require('minimist');

//获取参数
var argv = require('minimist')(process.argv.slice(2), {
  default: {
    ver: 'all' 
  }
})

//注释
,note = [
  // '/** <%= pkg.name %>-v<%= pkg.version %> <%= pkg.license %> License */\n <%= js %>'
  ,''
  ,{pkg: pkg, js: ';'}
]

,destDir = './dist' //构建的目标目录
,releaseDir = '../pack/layuiAdmin.pack/'+ pkg.name +'-v' + pkg.version //发行版本目录

//任务
,task = {
  //压缩 JS
  minjs: function(){
    var src = [
      './src/**/*.js'
      ,'!./src/config.js'
      ,'!./src/lib/extend/**/*.js'
    ];
    
    return gulp.src(src).pipe(uglify({
      output: {
        ascii_only: true //escape Unicode characters in strings and regexps
      }
    }))
     .pipe(header.apply(null, note))
    .pipe(gulp.dest(destDir));
  }
  
  //压缩 CSS
  ,mincss: function(){
    var src = [
      './src/**/*.css'
    ]
     ,noteNew = JSON.parse(JSON.stringify(note));
     
     
    noteNew[1].js = '';
    
    return gulp.src(src).pipe(minify({
      compatibility: 'ie7'
    })).pipe(header.apply(null, noteNew))
    .pipe(gulp.dest(destDir));
  }
  
  //复制文件夹
  ,mv: function(){    
    gulp.src('./src/config.js')
    .pipe(gulp.dest(destDir));
    
    gulp.src('./src/lib/extend/**/*')
    .pipe(gulp.dest(destDir + '/lib/extend'));
    
    gulp.src('./src/style/res/**/*')
    .pipe(gulp.dest(destDir + '/style/res'));
    
    return gulp.src('./src/views/**/*')
    .pipe(gulp.dest(destDir + '/views'));
  }
};

gulp.task('minjs', task.minjs);
gulp.task('mincss', task.mincss);
gulp.task('mv', task.mv);

//清理
gulp.task('clear', function(cb) {
  return del(['./dist/*'], cb);
});

//构建核心源文件（发行版）
gulp.task('default', ['clear'], function(){ //命令：gulp
  for(var key in task){
    task[key]();
  }
});



/**
 * 开发模式
 */

//清理 src
gulp.task('clearSrc', function(cb) {
  return del(['./src/*'], cb);
});

//复制 src
gulp.task('src', ['clearSrc'], function(){
  return gulp.src('./dev-src/**/*')
  .pipe(gulp.dest('./src'));
});

//构建核心源文件（开发版）
gulp.task('dev', ['clear', 'src'], function(){ //命令：gulp dev
  for(var key in task){
    task[key]();
  }
});

//发行文件
gulp.task('release', function(){ //命令：gulp && gulp release
  
  //复制核心文件
  gulp.src('./dist/**/*')
  .pipe(gulp.dest(releaseDir + '/dist'));
  
  gulp.src('./src/**/*')
  .pipe(gulp.dest(releaseDir + '/src'));

  //复制 json
  gulp.src('./dev/json/**/*')
  .pipe(gulp.dest('./json'))
  .pipe(gulp.dest('./start/json'))
  .pipe(gulp.dest(releaseDir + '/start/json'));

  //复制并转义宿主页面
  gulp.src('./dev/index.html')
    .pipe(replace(/\<\!-- clear s --\>([\s\S]*?)\<\!-- clear e --\>/, ''))
    .pipe(replace('//local.res.layui.com/layui/src', 'layui'))
    .pipe(replace("base: '../dev-src/'", "base: '../dist/'"))
    .pipe(replace('@@version@@', pkg.version))
  .pipe(gulp.dest('./start'))
  .pipe(gulp.dest(releaseDir + '/start'));
  
  //复制帮助文件
  gulp.src([
    './帮助/*'
  ]).pipe(gulp.dest(releaseDir));
  
  
  //复制 gulpfile
  gulp.src([
    'gulpfile.js'
    ,'package.json'
  ]).pipe(gulp.dest(releaseDir));
  
  //复制 layui
  return gulp.src('../../../../res/layui/rc/**/*')
  .pipe(gulp.dest('./start/layui'))
  .pipe(gulp.dest(releaseDir + '/start/layui'))
});

